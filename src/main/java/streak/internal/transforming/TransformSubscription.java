package streak.internal.transforming;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractChainedSubscription;

abstract class TransformSubscription<UpstreamT, DownstreamT>
  extends AbstractChainedSubscription
  implements Flow.Subscriber<UpstreamT>
{
  @Nonnull
  private final Flow.Subscriber<? super DownstreamT> _downstreamSubscriber;

  TransformSubscription( @Nonnull final Flow.Subscriber<? super DownstreamT> downstreamSubscriber )
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
  public void onError( @Nonnull final Throwable throwable )
  {
    getDownstreamSubscriber().onError( throwable );
  }

  /**
   * {@inheritDoc}
   */
  public void onComplete()
  {
    getDownstreamSubscriber().onComplete();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDisposed()
  {
    return getUpstream().isDisposed();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose()
  {
    if ( isNotDisposed() )
    {
      getUpstream().dispose();
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
}
