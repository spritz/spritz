package org.realityforge.rxs;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

final class PredicateFilterSubscriber<T>
  extends AbstractFilterSubscription<T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  PredicateFilterSubscriber( @Nonnull final Flow.Subscriber<? super T> subscriber,
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
