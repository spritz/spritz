package streak;

import javax.annotation.Nonnull;

abstract class AbstractPublisher<T>
  implements Flow.Publisher<T>
{
  @Nonnull
  @Override
  public Flow.Publisher<T> self()
  {
    return this;
  }
}
