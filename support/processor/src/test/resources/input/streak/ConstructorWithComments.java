package streak;

import javax.annotation.Nonnull;
import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

@MetaDataSource
public final class ConstructorWithComments
{
  private ConstructorWithComments()
  {
  }

  public interface Stream<T>
  {
  }

  /**
   * Creates a stream that emits the parameters as items and then emits the completion signal.
   *
   * @param <T>    the type of items contained in the stream.
   * @param values the values to emit.
   * @return the new stream.
   */
  @SafeVarargs
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> of( @Nonnull final T... values )
  {
    return null;
  }

  /**
   * Creates a stream that emits no items and immediately emits a completion signal.
   *
   * @param <T> the type of items that the stream declared as containing (despite never containing any items).
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> empty()
  {
    return null;
  }
}
