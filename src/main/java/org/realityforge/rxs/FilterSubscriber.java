package org.realityforge.rxs;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

final class FilterSubscriber<T>
  implements Flow.Subscriber<T>, Flow.Subscription
{
  @Nonnull
  private final Flow.Subscriber<? super T> _subscriber;
  @Nonnull
  private final Predicate<? super T> _predicate;
  private Flow.Subscription _subscription;
  private boolean _done;

  FilterSubscriber( @Nonnull final Flow.Subscriber<? super T> subscriber,
                    @Nonnull final Predicate<? super T> predicate )
  {
    _subscriber = Objects.requireNonNull( subscriber );
    _predicate = Objects.requireNonNull( predicate );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onSubscribe( @Nonnull final Flow.Subscription subscription )
  {
    _subscription = subscription;
    _subscriber.onSubscribe( this );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onNext( @Nonnull final T item )
  {
    if ( !_done )
    {
      try
      {
        if ( _predicate.test( item ) )
        {
          _subscriber.onNext( item );
        }
        else
        {
          _subscription.request( 1 );
        }
      }
      catch ( final Throwable throwable )
      {
        onError( throwable );
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onError( @Nonnull final Throwable throwable )
  {
    if ( !_done )
    {
      _done = true;
      _subscriber.onError( throwable );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onComplete()
  {
    if ( !_done )
    {
      _done = true;
      _subscriber.onComplete();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void request( final int count )
  {
    _subscription.request( count );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void cancel()
  {
    _subscription.cancel();
  }
}
