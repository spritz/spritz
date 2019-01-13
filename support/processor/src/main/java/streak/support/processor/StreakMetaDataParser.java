package streak.support.processor;

import javax.annotation.Nonnull;
import javax.lang.model.element.TypeElement;

final class StreakMetaDataParser
{
  private StreakMetaDataParser()
  {
  }

  @Nonnull
  static StreakMetaDataElement parse( @Nonnull final TypeElement typeElement )
  {
    return new StreakMetaDataElement( typeElement );
  }
}
