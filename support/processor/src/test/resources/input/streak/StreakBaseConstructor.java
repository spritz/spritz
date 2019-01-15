package streak;

import javax.annotation.Nonnull;
import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

@MetaDataSource
public final class StreakBaseConstructor
{
  private StreakBaseConstructor()
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
