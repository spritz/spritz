package streak;

import javax.annotation.Nonnull;

/**
 * A simple base class that can be used build streams.
 */
public abstract class AbstractStream<T>
  implements Stream<T>
{
  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public final Stream<T> self()
  {
    return this;
  }
}
