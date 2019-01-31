package spritz.elemental2;

import javax.annotation.Nonnull;
import spritz.Stream;
import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public class Elemental2Sources
{
  /**
   * Placeholder elemental2 stream constructor.
   */
  @SafeVarargs
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> elemental2( @Nonnull final T... values )
  {
    return Stream.of( values );
  }
}
