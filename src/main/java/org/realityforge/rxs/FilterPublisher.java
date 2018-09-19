package org.realityforge.rxs;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

final class FilterPublisher<T>
  extends PublisherWithUpstream<T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  FilterPublisher( @Nonnull final Flow.Publisher<? extends T> upstream, @Nonnull final Predicate<? super T> predicate )
  {
    super( upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new PredicateFilterSubscriber<>( subscriber, _predicate ) );
  }
}
