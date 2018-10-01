package streak;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import org.realityforge.braincheck.Guards;

abstract class SubscriptionWithDownstream<T>
  implements Flow.Subscription, Flow.Subscriber<T>
{
  @Nonnull
  private final Flow.Subscriber<? super T> _downstreamSubscriber;
  @Nullable
  private Flow.Subscription _upstreamSubscription;
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
    _upstreamSubscription = subscription;
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
  public void request( final int count )
  {
    getUpstreamSubscription().request( count );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose()
  {
    _done = true;
    getUpstreamSubscription().dispose();
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

  /**
   * Return the subscription used to interact with the upstream publisher.
   *
   * @return the subscription used to interact with the upstream publisher.
   */
  @Nonnull
  final Flow.Subscription getUpstreamSubscription()
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      Guards.invariant( () -> null != _upstreamSubscription,
                        () -> "Streak-0002: Attempted to invoke getUpstreamSubscription() when subscription is not present" );
    }
    assert null != _upstreamSubscription;
    return _upstreamSubscription;
  }
}
