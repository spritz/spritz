package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

abstract class SubscriptionWithDownstream<T>
  extends AbstractChainedSubscription
  implements Flow.Subscriber<T>
{
  @Nonnull
  private final Flow.Subscriber<? super T> _downstreamSubscriber;
  private boolean _done;

  SubscriptionWithDownstream( @Nonnull final Flow.Subscriber<? super T> downstreamSubscriber )
  {
    _downstreamSubscriber = Objects.requireNonNull( downstreamSubscriber );
  }

  /**
   * {@inheritDoc}
   */
  public void onSubscribe( @Nonnull final Flow.Subscription subscription )
  {
    setUpstream( subscription );
    _downstreamSubscriber.onSubscribe( this );
  }

  /**
   * {@inheritDoc}
   */
  public void onNext( @Nonnull final T item )
  {
    if ( isNotDisposed() )
    {
      getDownstreamSubscriber().onNext( item );
    }
  }

  /**
   * {@inheritDoc}
   */
  public void onError( @Nonnull final Throwable throwable )
  {
    if ( isNotDisposed() )
    {
      _done = true;
      getDownstreamSubscriber().onError( throwable );
    }
  }

  /**
   * {@inheritDoc}
   */
  public void onComplete()
  {
    if ( isNotDisposed() )
    {
      _done = true;
      getDownstreamSubscriber().onComplete();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose()
  {
    _done = true;
    getUpstream().dispose();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDisposed()
  {
    return _done;
  }

  /**
   * Return the downstream subscriber.
   *
   * @return the downstream subscriber.
   */
  @Nonnull
  final Flow.Subscriber<? super T> getDownstreamSubscriber()
  {
    return _downstreamSubscriber;
  }
}
