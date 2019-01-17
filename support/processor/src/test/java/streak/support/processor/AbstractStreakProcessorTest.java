package streak.support.processor;

import com.google.common.collect.ImmutableList;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.tools.JavaFileObject;
import static com.google.common.truth.Truth.*;
import static org.testng.Assert.*;

abstract class AbstractStreakProcessorTest
{
  void assertSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    final String[] elements = classname.contains( "." ) ? classname.split( "\\." ) : new String[]{ classname };
    final StringBuilder input = new StringBuilder();
    final StringBuilder docFile = new StringBuilder();
    input.append( "input" );
    docFile.append( "expected" );

    for ( final String element : elements )
    {
      input.append( '/' );
      input.append( element );
      docFile.append( '/' );
      docFile.append( element );
    }
    input.append( ".java" );
    docFile.append( ".doc.json" );
    final ArrayList<String> expectedOutputs = new ArrayList<>();
    expectedOutputs.add( docFile.toString() );

    // The annotations have been copied into test directory as they are not part of the API
    // but they are part of our mechanism for validating the project. We MUST keep these
    // aligned else bad things will happen.
    assertSuccessfulCompile( Arrays.asList( fixture( "input/streak/internal/annotations/DocCategory.java" ),
                                            fixture( "input/streak/internal/annotations/MetaDataSource.java" ),
                                            fixture( "input/streak/internal/annotations/SourceCategory.java" ),
                                            fixture( input.toString() ) ),
                             expectedOutputs );
  }

  private void assertSuccessfulCompile( @Nonnull final List<JavaFileObject> inputs,
                                        @Nonnull final List<String> outputs )
    throws Exception
  {
    if ( outputFiles() )
    {
      final Compilation compilation =
        Compiler.javac().withProcessors( new StreakProcessor() ).compile( inputs );

      final Compilation.Status status = compilation.status();
      if ( Compilation.Status.SUCCESS != status )
      {
        /*
         * Ugly hackery that marks the compile as successful so we can emit output onto filesystem. This could
         * result in java code that is not compilable emitted to filesystem. This re-running determining problems
         * a little easier even if it does make re-running tests from IDE a little harder
         */
        final Field field = compilation.getClass().getDeclaredField( "status" );
        field.setAccessible( true );
        field.set( compilation, Compilation.Status.SUCCESS );
      }

      final ImmutableList<JavaFileObject> fileObjects = compilation.generatedFiles();
      for ( final JavaFileObject fileObject : fileObjects )
      {
        if ( fileObject.getKind() != JavaFileObject.Kind.CLASS )
        {
          final Path target =
            fixtureDir().resolve( "expected/" + fileObject.getName().replace( "/SOURCE_OUTPUT/", "" ) );

          final File dir = target.getParent().toFile();
          if ( !dir.exists() )
          {
            assertTrue( dir.mkdirs() );
          }
          if ( Files.exists( target ) )
          {
            final byte[] existing = Files.readAllBytes( target );
            final InputStream generated = fileObject.openInputStream();
            final byte[] data = new byte[ generated.available() ];
            assertEquals( generated.read( data ), data.length );
            if ( Arrays.equals( existing, data ) )
            {
              /*
               * If the data on the filesystem is identical to data generated then do not write
               * to filesystem. The writing can be slow and it can also trigger the IDE or other
               * tools to recompile code which is problematic.
               */
              continue;
            }
            Files.delete( target );
          }
          Files.copy( fileObject.openInputStream(), target );
        }
      }

      if ( Compilation.Status.SUCCESS != status )
      {
        // Restore old status
        final Field field = compilation.getClass().getDeclaredField( "status" );
        field.setAccessible( true );
        field.set( compilation, status );

        // This next line will generate an error
        //noinspection ResultOfMethodCallIgnored
        compilation.generatedSourceFiles();
      }
    }
    final JavaFileObject firstExpected = fixture( outputs.get( 0 ) );
    final JavaFileObject[] restExpected =
      outputs.stream().skip( 1 ).map( this::fixture ).toArray( JavaFileObject[]::new );
    assert_().about( JavaSourcesSubjectFactory.javaSources() ).
      that( inputs ).
      processedWith( new StreakProcessor() ).
      compilesWithoutError().
      and().
      generatesFiles( firstExpected, restExpected );
  }

  @Nonnull
  private JavaFileObject fixture( @Nonnull final String path )
  {
    final Path outputFile = fixtureDir().resolve( path );
    if ( !Files.exists( outputFile ) )
    {
      fail( "Fixture file " + outputFile + " does not exist. Thus can not compare against it." );
    }
    try
    {
      return JavaFileObjects.forResource( outputFile.toUri().toURL() );
    }
    catch ( final MalformedURLException e )
    {
      throw new IllegalStateException( e );
    }
  }

  @Nonnull
  private Path fixtureDir()
  {
    final String fixtureDir = System.getProperty( "streak.fixture_dir" );
    assertNotNull( fixtureDir, "Expected System.getProperty( \"streak.fixture_dir\" ) to return fixture directory" );
    return new File( fixtureDir ).toPath();
  }

  private boolean outputFiles()
  {
    return System.getProperty( "streak.output_fixture_data", "false" ).equals( "true" );
  }
}
