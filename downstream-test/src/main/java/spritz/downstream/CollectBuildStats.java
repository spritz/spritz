package spritz.downstream;

import gir.Gir;
import gir.git.Git;
import gir.io.Exec;
import gir.io.FileUtil;
import gir.maven.Maven;
import gir.ruby.Buildr;
import gir.ruby.Ruby;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public final class CollectBuildStats
{
  public static void main( final String[] args )
  {
    try
    {
      run();
    }
    catch ( final Exception e )
    {
      System.err.println( "Failed command." );
      e.printStackTrace( System.err );
      System.exit( 42 );
    }
  }

  private static void run()
    throws Exception
  {
    Gir.go( () -> {
      final List<String> branches = Arrays.asList( "raw", "spritz" );

      WorkspaceUtil.forEachBranch( "react4j-todomvc",
                                   "https://github.com/react4j/react4j-todomvc.git",
                                   branches,
                                   context -> buildBranch( context, WorkspaceUtil.getVersion() ) );
      WorkspaceUtil.collectStatistics( branches, branch -> !branch.endsWith( "_maven" ), true );
    } );
  }

  private static void buildBranch( @Nonnull final WorkspaceUtil.BuildContext context, @Nonnull final String version )
  {
    final boolean isMaven = context.branch.contains( "_maven" );
    final boolean isj2cl = context.branch.contains( "_j2cl" );

    final boolean initialBuildSuccess = WorkspaceUtil.runBeforeBuild( context, () -> {
      final String prefix = context.branch + ".before";
      final Path archiveDir = WorkspaceUtil.getArchiveDir( context.workingDirectory, prefix );
      buildAndRecordStatistics( context.appDirectory, archiveDir, !isMaven, isj2cl );
    } );

    WorkspaceUtil.runAfterBuild( context, initialBuildSuccess, () -> {
      if ( isMaven )
      {
        Maven.patchPomProperty( context.appDirectory,
                                () -> "Update the 'spritz' dependencies to version '" + version + "'",
                                "spritz.version",
                                version );
      }
      else
      {
        Buildr.patchBuildYmlDependency( context.appDirectory, "org.realityforge.spritz", version );
      }

      final String prefix = context.branch + ".after";
      final Path archiveDir = WorkspaceUtil.getArchiveDir( context.workingDirectory, prefix );
      buildAndRecordStatistics( context.appDirectory, archiveDir, !isMaven, isj2cl );
      if ( isMaven )
      {
        // Reset is required to remove changes that were made to the pom to add local repository
        Git.resetBranch();
      }
    }, () -> FileUtil.deleteDir( WorkspaceUtil.getArchiveDir( context.workingDirectory, context.branch + ".after" ) ) );
  }

  private static void buildAndRecordStatistics( @Nonnull final Path appDirectory,
                                                @Nonnull final Path archiveDir,
                                                final boolean useBuildr,
                                                final boolean isj2cl )
  {
    if ( useBuildr )
    {
      WorkspaceUtil.customizeBuildr( appDirectory );
    }
    else
    {
      WorkspaceUtil.customizeMaven( appDirectory );
    }

    if ( !archiveDir.toFile().mkdirs() )
    {
      final String message = "Error creating archive directory: " + archiveDir;
      Gir.messenger().error( message );
    }

    if ( useBuildr )
    {
      // Perform the build
      Ruby.buildr( "clean", "package", "EXCLUDE_GWT_DEV_MODULE=true", "GWT=react4j-todomvc" );

      archiveBuildrOutput( archiveDir );
    }
    else if ( isj2cl )
    {
      // Clean any artifacts hanging around that are not cleaned by clean maven action
      final Path currentDirectory = FileUtil.getCurrentDirectory();
      FileUtil.inDirectory( currentDirectory, () -> {
        final Path appJs = currentDirectory.resolve( "out" ).resolve( "app.js" );
        if ( appJs.toFile().exists() )
        {
          //noinspection ResultOfMethodCallIgnored
          appJs.toFile().delete();
        }
        FileUtil.deleteDirIfExists( currentDirectory.resolve( "jsZipCache" ) );
        FileUtil.deleteDirIfExists( currentDirectory.resolve( "out" ).resolve( "sources" ) );
      } );
      // Assume maven
      Exec.system( "mvn", "clean", "package", "-Pdevmode" );

      archivej2clOutput( archiveDir );
    }
    else
    {
      // Assume maven
      Exec.system( "mvn", "clean", "package" );

      archiveMavenOutput( archiveDir );
    }
    archiveStatistics( archiveDir );
  }

  private static void archiveStatistics( @Nonnull final Path archiveDir )
  {
    final OrderedProperties properties = new OrderedProperties();
    properties.setProperty( "todomvc.size", String.valueOf( getTodoMvcSize( archiveDir ) ) );
    final long todoMvcGzSize = getTodoMvcGzSize( archiveDir );
    if ( 0 != todoMvcGzSize )
    {
      //j2cl does not produce a gzipped version so no need to include 0 all the time
      properties.setProperty( "todomvc.gz.size", String.valueOf( todoMvcGzSize ) );
    }

    final Path statisticsFile = archiveDir.resolve( "statistics.properties" );
    Gir.messenger().info( "Archiving statistics to " + statisticsFile + "." );
    WorkspaceUtil.writeProperties( statisticsFile, properties );
  }

  private static void archiveBuildrOutput( @Nonnull final Path archiveDir )
  {
    final Path currentDirectory = FileUtil.getCurrentDirectory();
    WorkspaceUtil.archiveDirectory( currentDirectory.resolve( "target/assets" ), archiveDir.resolve( "assets" ) );
    WorkspaceUtil.archiveDirectory( currentDirectory.resolve( "target/gwt_compile_reports/react4j.todomvc.TodomvcProd" ),
                                    archiveDir.resolve( "compileReports" ) );
  }

  private static void archivej2clOutput( @Nonnull final Path archiveDir )
  {
    WorkspaceUtil.archiveDirectory( FileUtil.getCurrentDirectory().resolve( "out/sources" ),
                                    archiveDir.resolve( "sources" ) );
    WorkspaceUtil.archiveFile( FileUtil.getCurrentDirectory().resolve( "out/app.js" ),
                               archiveDir.resolve( "assets/todomvc/todomvc.nocache.js" ) );
    WorkspaceUtil.archiveFile( FileUtil.getCurrentDirectory().resolve( "out/app.map" ),
                               archiveDir.resolve( "assets/todomvc/todomvc.nocache.map" ) );
  }

  private static void archiveMavenOutput( @Nonnull final Path archiveDir )
  {
    WorkspaceUtil.archiveDirectory( FileUtil.getCurrentDirectory().resolve( "target/react4j-todomvc-1.0.0-SNAPSHOT" ),
                                    archiveDir.resolve( "assets" ) );
    WorkspaceUtil.archiveDirectory( FileUtil.getCurrentDirectory().resolve( "target/extras" ),
                                    archiveDir.resolve( "compileReports" ) );
  }

  private static long getTodoMvcSize( @Nonnull final Path archiveDir )
  {
    return WorkspaceUtil.getFileSize( archiveDir.resolve( "assets" )
                                        .resolve( "todomvc" )
                                        .resolve( "todomvc.nocache.js" ) );
  }

  private static long getTodoMvcGzSize( @Nonnull final Path archiveDir )
  {
    return WorkspaceUtil.getFileSize( archiveDir.resolve( "assets" )
                                        .resolve( "todomvc" )
                                        .resolve( "todomvc.nocache.js.gz" ) );
  }
}
