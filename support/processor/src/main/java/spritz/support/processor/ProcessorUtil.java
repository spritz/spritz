package spritz.support.processor;

import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;

@SuppressWarnings( "SameParameterValue" )
final class ProcessorUtil
{
  private ProcessorUtil()
  {
  }

  @Nullable
  static AnnotationMirror findAnnotationByType( @Nonnull final AnnotatedConstruct annotated,
                                                @Nonnull final String annotationClassName )
  {
    return annotated.getAnnotationMirrors().stream().
      filter( a -> a.getAnnotationType().toString().equals( annotationClassName ) ).findFirst().orElse( null );
  }

  @Nullable
  static AnnotationValue findAnnotationValueNoDefaults( @Nonnull final AnnotationMirror annotation,
                                                        @Nonnull final String parameterName )
  {
    final Map<? extends ExecutableElement, ? extends AnnotationValue> values = annotation.getElementValues();
    final ExecutableElement annotationKey = values.keySet().stream().
      filter( k -> parameterName.equals( k.getSimpleName().toString() ) ).findFirst().orElse( null );
    return values.get( annotationKey );
  }
}
