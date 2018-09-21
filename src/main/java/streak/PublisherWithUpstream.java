package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

abstract class PublisherWithUpstream<T>
  extends AbstractPublisher<T>
{
  @Nonnull
  private final Flow.Publisher<? extends T> _upstream;

  PublisherWithUpstream( @Nonnull final Flow.Publisher<? extends T> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  @Nonnull
  final Flow.Publisher<? extends T> getUpstream()
  {
    return _upstream;
  }
}
