package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract implementation for subscription with both an upstream and downstream stream stage.
 */
abstract class AbstractOperatorSubscription<T>
  extends AbstractSubscription
  implements Subscriber<T>
{
  /**
   * The subscriber for the downstream stream.
   */
  @Nonnull
  private final Subscriber<? super T> _downstreamSubscriber;
  private boolean _done;

  /**
   * Create the subscription with the downstream subscriber.
   *
   * @param downstreamSubscriber the downstream subscriber.
   */
  AbstractOperatorSubscription( @Nonnull final Subscriber<? super T> downstreamSubscriber )
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
  public void onNext( @Nonnull final T item )
  {
    getDownstreamSubscriber().onNext( item );
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
  final Subscriber<? super T> getDownstreamSubscriber()
  {
    return _downstreamSubscriber;
  }
}