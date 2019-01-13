package streak.support.processor;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
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
    throws IOException
  {
    final StreakMetaDataElement metaData = StreakMetaDataParser.parse( element );
    writeJsonData( metaData.getTypeElement(), writer -> emitMetaData( metaData, writer ) );
  }

  private void emitMetaData( @Nonnull final StreakMetaDataElement metaData, @Nonnull final JsonWriter writer )
  {
    final JsonObjectBuilder object = Json.createObjectBuilder();
    object.add( "class", metaData.getTypeElement().getQualifiedName().toString() );
    writer.writeObject( object.build() );
  }

  private void writeJsonData( @Nonnull final TypeElement element,
                              @Nonnull final Consumer<JsonWriter> writeBlock )
    throws IOException
  {
    final FileObject resource =
      processingEnv
        .getFiler()
        .createResource( StandardLocation.SOURCE_OUTPUT,
                         element.getEnclosingElement().getSimpleName(),
                         element.getSimpleName() + ".doc.json",
                         element );
    final JsonWriter writer = Json.createWriter( resource.openWriter() );
    writeBlock.accept( writer );
    writer.close();
  }
}
