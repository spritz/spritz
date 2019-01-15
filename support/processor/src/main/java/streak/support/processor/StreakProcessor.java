package streak.support.processor;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.BreakIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import static javax.tools.Diagnostic.Kind.*;

/**
 * Annotation processor that analyzes Streak source and generates documentation and test infrastructure.
 */
@AutoService( Processor.class )
@SupportedAnnotationTypes( Constants.META_DATA_SOURCE )
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
      processingEnv.getElementUtils().getTypeElement( Constants.META_DATA_SOURCE );
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
    final ClassDescriptor metaData = parseClassDescriptor( element );
    writeJsonData( metaData.getTypeElement(), metaData::write );
  }

  @Nonnull
  private ClassDescriptor parseClassDescriptor( @Nonnull final TypeElement element )
  {
    final ClassDescriptor metaData = new ClassDescriptor( element );
    for ( final Element member : element.getEnclosedElements() )
    {
      if ( member.getKind() == ElementKind.METHOD )
      {
        processMethod( metaData, (ExecutableElement) member );
      }
    }
    return metaData;
  }

  private void processMethod( final ClassDescriptor metaData, final ExecutableElement method )
  {
    final AnnotationMirror annotation = ProcessorUtil.findAnnotationByType( method, Constants.DOC_CATEGORY );
    final ExecutableType methodType =
      (ExecutableType) processingEnv.getTypeUtils()
        .asMemberOf( (DeclaredType) metaData.getTypeElement().asType(), method );
    if ( null != annotation )
    {
      final String docComment = processingEnv.getElementUtils().getDocComment( method );
      final String description;
      if ( null != docComment )
      {
        final BreakIterator boundary = BreakIterator.getSentenceInstance();
        boundary.setText( docComment );
        final int start = boundary.first();
        final int end = boundary.next();
        final String firstLine =
          ( BreakIterator.DONE == end ? docComment.substring( start ) : docComment.substring( start, end ) ).trim();
        description =
          firstLine.charAt( firstLine.length() - 1 ) == '.' ?
          firstLine.substring( 0, firstLine.length() - 1 ) :
          firstLine;
      }
      else
      {
        description = "";
      }
      final OperatorDescriptor operator = new OperatorDescriptor( method, methodType, description );
      metaData.addOperator( operator );
      final AnnotationValue value = ProcessorUtil.findAnnotationValueNoDefaults( annotation, "value" );
      assert null != value;
      @SuppressWarnings( "unchecked" )
      final List<AnnotationValue> categories = (List<AnnotationValue>) value.getValue();
      for ( final AnnotationValue category : categories )
      {
        operator.addCategory( category.getValue().toString() );
      }
    }
  }

  private void writeJsonData( @Nonnull final TypeElement element,
                              @Nonnull final Consumer<JsonGenerator> writeBlock )
    throws IOException
  {
    final FileObject resource =
      processingEnv
        .getFiler()
        .createResource( StandardLocation.SOURCE_OUTPUT,
                         element.getEnclosingElement().getSimpleName(),
                         element.getSimpleName() + ".doc.json",
                         element );
    final HashMap<String, Object> config = new HashMap<>();
    config.put( JsonGenerator.PRETTY_PRINTING, Boolean.TRUE );
    final JsonGeneratorFactory factory = Json.createGeneratorFactory( config );
    final OutputStream outputStream = resource.openOutputStream();
    final JsonGenerator generator = factory.createGenerator( outputStream );
    writeBlock.accept( generator );
    generator.flush();
    outputStream.write( '\n' );
    generator.close();
  }
}
