package streak.internal;

import javax.annotation.Nonnull;
import streak.Flow;

/**
 * A simple base class that can be used build streams.
 */
public abstract class AbstractStream<T>
  implements Flow.Stream<T>
{
  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public final Flow.Stream<T> self()
  {
    return this;
  }
}
