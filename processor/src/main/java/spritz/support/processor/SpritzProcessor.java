package spritz.support.processor;

import com.google.auto.service.AutoService;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.BreakIterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import static javax.tools.Diagnostic.Kind.*;

/**
 * Annotation processor that analyzes Spritz source and generates documentation and test infrastructure.
 */
@AutoService( Processor.class )
@SupportedAnnotationTypes( Constants.META_DATA_SOURCE )
@SupportedSourceVersion( SourceVersion.RELEASE_8 )
public final class SpritzProcessor
  extends AbstractProcessor
{
  @FunctionalInterface
  interface Action<T>
  {
    void accept( T t )
      throws Throwable;
  }

  @Override
  public boolean process( final Set<? extends TypeElement> annotations, final RoundEnvironment env )
  {
    final TypeElement annotation =
      processingEnv.getElementUtils().getTypeElement( Constants.META_DATA_SOURCE );
    final Set<? extends Element> elements = env.getElementsAnnotatedWith( annotation );
    processElements( elements );
    if ( env.processingOver() )
    {
      processDocCategoryIfPresent();
    }
    return true;
  }

  private void processDocCategoryIfPresent()
  {
    // If we are compiling the DocCategory annotation then lets extract metadata from it
    final TypeElement docCategory = processingEnv.getElementUtils().getTypeElement( Constants.DOC_CATEGORY );
    if ( null != docCategory )
    {
      try
      {
        final TypeElement typeTypeElement = (TypeElement)
          docCategory.getEnclosedElements()
            .stream()
            .filter( e -> "Type".equals( e.getSimpleName().toString() ) )
            .findFirst().orElse( null );
        assert null != typeTypeElement;
        writeJsonData( docCategory, g -> emitCategoryEnums( g, docCategory, typeTypeElement ) );
      }
      catch ( final Throwable e )
      {
        processingEnv.getMessager().printMessage( ERROR, generateFatalErrorMessage( e ), docCategory );
      }
    }
  }

  private void emitCategoryEnums( @Nonnull final JsonGenerator generator,
                                  @Nonnull final TypeElement typeElement,
                                  @Nonnull final TypeElement typeTypeElement )
  {
    generator.writeStartObject();
    generator.write( "class", typeElement.getQualifiedName().toString() );
    generator.writeStartArray( "categories" );
    int index = 0;
    for ( final Element enclosedElement : typeTypeElement.getEnclosedElements() )
    {
      if ( enclosedElement instanceof VariableElement )
      {
        final VariableElement variableElement = (VariableElement) enclosedElement;
        if ( variableElement.getModifiers().contains( Modifier.STATIC ) )
        {
          final String name = variableElement.getSimpleName().toString();

          generator.writeStartObject();
          generator.write( "id", index );
          generator.write( "name", name );
          generator.write( "description", getDescription( variableElement ) );
          final boolean isSourceCategory =
            null != ProcessorUtil.findAnnotationByType( variableElement, Constants.SOURCE_CATEGORY );
          generator.write( "type", isSourceCategory ? "source" : "operator" );
          generator.writeEnd();
          index++;
        }
      }
    }
    generator.writeEnd();
    generator.writeEnd();
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
        processingEnv.getMessager().printMessage( ERROR, generateFatalErrorMessage( e ), element );
      }
    }
  }

  @Nonnull
  private String generateFatalErrorMessage( @Nonnull final Throwable e )
  {
    final StringWriter sw = new StringWriter();
    e.printStackTrace( new PrintWriter( sw ) );
    sw.flush();

    return "Unexpected error running the " + getClass().getName() + " processor. This has " +
           "resulted in a failure to process the code and has left the compiler in an invalid " +
           "state. Please report the failure to the developers so that it can be fixed.\n" +
           " Report the error at: https://github.com/realityforge/spritz/issues\n" +
           "\n\n" +
           sw.toString();
  }

  private void process( @Nonnull final TypeElement element )
    throws Throwable
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
      final String description = getDescription( method );
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

  private String getDescription( final Element method )
  {
    final String docComment = processingEnv.getElementUtils().getDocComment( method );
    return null != docComment ? extractFirstSentence( docComment ) : "";
  }

  @Nonnull
  private String extractFirstSentence( @Nonnull final String text )
  {
    final String description;
    final BreakIterator boundary = BreakIterator.getSentenceInstance();
    boundary.setText( text );
    final int start = boundary.first();
    final int end = boundary.next();
    final String firstLine =
      ( BreakIterator.DONE == end ? text.substring( start ) : text.substring( start, end ) ).trim();
    description =
      firstLine.charAt( firstLine.length() - 1 ) == '.' ?
      firstLine.substring( 0, firstLine.length() - 1 ) :
      firstLine;
    return description;
  }

  private void writeJsonData( @Nonnull final TypeElement element, @Nonnull final Action<JsonGenerator> writeBlock )
    throws Throwable
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
