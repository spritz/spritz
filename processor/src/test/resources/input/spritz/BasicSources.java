package spritz;

import javax.annotation.Nonnull;
import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public final class BasicSources
{
  private BasicSources()
  {
  }

  public interface Stream<T>
  {
  }

  @SafeVarargs
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> of( @Nonnull final T... values )
  {
    return null;
  }

  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> empty()
  {
    return null;
  }

  @DocCategory( DocCategory.Type.CONSTRUCTION )
  @SafeVarargs
  public static <T> Stream<T> concat( @Nonnull final Stream<T>... upstreams )
  {
    return null;
  }
}
