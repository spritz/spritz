package streak.internal;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;

public abstract class StreamWithUpstream<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Flow.Stream<? extends T> _upstream;

  protected StreamWithUpstream( @Nonnull final Flow.Stream<? extends T> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  @Nonnull
  protected final Flow.Stream<? extends T> getUpstream()
  {
    return _upstream;
  }
}
