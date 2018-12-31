package streak.internal;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;

public abstract class PublisherWithUpstream<T>
  extends AbstractPublisher<T>
{
  @Nonnull
  private final Flow.Stream<? extends T> _upstream;

  protected PublisherWithUpstream( @Nonnull final Flow.Stream<? extends T> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  @Nonnull
  protected final Flow.Stream<? extends T> getUpstream()
  {
    return _upstream;
  }
}
