package streak.internal;

import javax.annotation.Nonnull;
import streak.Flow;

public abstract class AbstractPublisher<T>
  implements Flow.Stream<T>
{
  @Nonnull
  @Override
  public final Flow.Stream<T> self()
  {
    return this;
  }
}
