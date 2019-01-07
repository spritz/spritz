package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

abstract class TransformSubscription<UpstreamT, DownstreamT>
  extends AbstractChainedSubscription
  implements Subscriber<UpstreamT>
{
  @Nonnull
  private final Subscriber<? super DownstreamT> _downstreamSubscriber;

  TransformSubscription( @Nonnull final Subscriber<? super DownstreamT> downstreamSubscriber )
  {
    _downstreamSubscriber = Objects.requireNonNull( downstreamSubscriber );
  }

  /**
   * {@inheritDoc}
   */
  public void onSubscribe( @Nonnull final Subscription subscription )
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
  final Subscriber<? super DownstreamT> getDownstreamSubscriber()
  {
    return _downstreamSubscriber;
  }
}
