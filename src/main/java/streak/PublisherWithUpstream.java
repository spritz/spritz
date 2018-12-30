package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

abstract class PublisherWithUpstream<T>
  extends AbstractPublisher<T>
{
  @Nonnull
  private final Flow.Stream<? extends T> _upstream;

  PublisherWithUpstream( @Nonnull final Flow.Stream<? extends T> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  @Nonnull
  final Flow.Stream<? extends T> getUpstream()
  {
    return _upstream;
  }
}
