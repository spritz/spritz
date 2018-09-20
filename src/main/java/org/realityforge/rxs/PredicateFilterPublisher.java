package org.realityforge.rxs;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

final class PredicateFilterPublisher<T>
  extends PublisherWithUpstream<T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  PredicateFilterPublisher( @Nonnull final Flow.Publisher<? extends T> upstream,
                            @Nonnull final Predicate<? super T> predicate )
  {
    super( upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new PredicateFilterSubscription<>( subscriber, _predicate ) );
  }

  private static final class PredicateFilterSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    @Nonnull
    private final Predicate<? super T> _predicate;

    PredicateFilterSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber,
                                 @Nonnull final Predicate<? super T> predicate )
    {
      super( subscriber );
      _predicate = Objects.requireNonNull( predicate );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      return _predicate.test( item );
    }
  }
}
