package streak;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import org.realityforge.braincheck.Guards;

abstract class TransformSubscription<UpstreamT, DownstreamT>
  implements Flow.Subscription, Flow.Subscriber<UpstreamT>
{
  @Nonnull
  private final Flow.Subscriber<? super DownstreamT> _downstreamSubscriber;
  @Nullable
  private Flow.Subscription _upstreamSubscription;
  private boolean _done;

  TransformSubscription( @Nonnull final Flow.Subscriber<? super DownstreamT> downstreamSubscriber )
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
  public void onError( @Nonnull final Throwable throwable )
  {
    if ( isNotDisposed() )
    {
      markAsDone();
      getDownstreamSubscriber().onError( throwable );
    }
  }

  protected void markAsDone()
  {
    _done = true;
  }

  /**
   * {@inheritDoc}
   */
  public void onComplete()
  {
    if ( isNotDisposed() )
    {
      markAsDone();
      getDownstreamSubscriber().onComplete();
    }
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
   * {@inheritDoc}
   */
  @Override
  public void dispose()
  {
    if ( isNotDisposed() )
    {
      getUpstreamSubscription().dispose();
    }
  }

  /**
   * Return the downstream subscriber.
   *
   * @return the downstream subscriber.
   */
  @Nonnull
  final Flow.Subscriber<? super DownstreamT> getDownstreamSubscriber()
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
