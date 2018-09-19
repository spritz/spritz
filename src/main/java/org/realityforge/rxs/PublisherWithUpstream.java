package org.realityforge.rxs;

import java.util.Objects;
import java.util.function.Predicate;
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
  protected final Flow.Publisher<? extends T> getUpstream()
  {
    return _upstream;
  }
}
