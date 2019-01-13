package streak.support.processor;

import com.google.auto.service.AutoService;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import static javax.tools.Diagnostic.Kind.*;

/**
 * Annotation processor that analyzes Streak source and generates documentation and test infrastructure.
 */
@AutoService( Processor.class )
@SupportedAnnotationTypes( "streak.MetaDataSource" )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public final class StreakProcessor
  extends AbstractProcessor
{
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    final TypeElement annotation =
      processingEnv.getElementUtils().getTypeElement( "streak.MetaDataSource" );
    final Set<? extends Element> elements = env.getElementsAnnotatedWith( annotation );
    processElements( elements );
    return true;
  }

  private void processElements( @Nonnull final Collection<? extends Element> elements )
  {
    for ( final Element element : elements )
    {
      try
      {
        process( (TypeElement) element );
      }
      catch ( final Throwable e )
      {
        final StringWriter sw = new StringWriter();
        e.printStackTrace( new PrintWriter( sw ) );
        sw.flush();

        final String message =
          "Unexpected error will running the " + getClass().getName() + " processor. This has " +
          "resulted in a failure to process the code and has left the compiler in an invalid " +
          "state. Please report the failure to the developers so that it can be fixed.\n" +
          " Report the error at: https://github.com/realityforge/streak/issues\n" +
          "\n\n" +
          sw.toString();
        processingEnv.getMessager().printMessage( ERROR, message, element );
      }
    }
  }

  private void process( @Nonnull final TypeElement element )
  {
    final String typeName = element.getQualifiedName().toString();
    final boolean isStreak = "streak.Streak".equals( typeName );
    if ( isStreak )
    {
      processStreamSources( element );
    }
    else
    {
      processStreamOperators( element );
    }
  }

  private void processStreamSources( @Nonnull final TypeElement element )
  {
  }

  private void processStreamOperators( @Nonnull final TypeElement element )
  {
  }
}
