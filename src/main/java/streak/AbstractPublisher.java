package streak;

import javax.annotation.Nonnull;

abstract class AbstractPublisher<T>
  implements Flow.Stream<T>
{
  @Nonnull
  @Override
  public Flow.Stream<T> self()
  {
    return this;
  }
}
