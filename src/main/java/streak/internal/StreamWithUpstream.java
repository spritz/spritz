package streak.internal;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Stream;

public abstract class StreamWithUpstream<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Stream<? extends T> _upstream;

  protected StreamWithUpstream( @Nonnull final Stream<? extends T> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  @Nonnull
  protected final Stream<? extends T> getUpstream()
  {
    return _upstream;
  }
}
