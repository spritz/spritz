package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

abstract class TransformSubscription<UpstreamT, DownstreamT>
  extends AbstractSubscription
  implements Subscriber<UpstreamT>
{
  @Nonnull
  private final Subscriber<? super DownstreamT> _downstreamSubscriber;
  private boolean _done;

  TransformSubscription( @Nonnull final Subscriber<? super DownstreamT> downstreamSubscriber )
  {
    _downstreamSubscriber = Objects.requireNonNull( downstreamSubscriber );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    setUpstream( subscription );
    _downstreamSubscriber.onSubscribe( this );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onError( @Nonnull final Throwable throwable )
  {
    _done = true;
    getDownstreamSubscriber().onError( throwable );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onComplete()
  {
    _done = true;
    getDownstreamSubscriber().onComplete();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void cancel()
  {
    if ( !_done )
    {
      _done = true;
      getUpstream().cancel();
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
