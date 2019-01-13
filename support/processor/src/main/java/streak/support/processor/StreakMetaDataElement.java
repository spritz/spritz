package streak.support.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

final class StreakMetaDataElement
{
  @Nonnull
  private final TypeElement _typeElement;

  StreakMetaDataElement( @Nonnull final TypeElement typeElement )
  {
    _typeElement = typeElement;
  }

  @Nonnull
  TypeElement getTypeElement()
  {
    return _typeElement;
  }
}
