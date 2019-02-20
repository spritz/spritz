package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.Guards;

/**
 * Abstract implementation for subscription with both an upstream and downstream stream stage.
 */
abstract class AbstractOperatorSubscription<UpstreamT, DownstreamT>
  extends AbstractSubscription
  implements Subscriber<UpstreamT>
{
  /**
   * The subscriber for the downstream stream.
   */
  @Nonnull
  private final Subscriber<? super DownstreamT> _downstreamSubscriber;
  /**
   * The upstream subscription.
   */
  @Nullable
  private Subscription _upstream;

  /**
   * Set the upstream subscription.
   * This method is expected to be invoked as the first part of the {@link Subscriber#onSubscribe(Subscription)}
   * step.
   *
   * @param upstream the upstream subscription.
   */
  protected final void setUpstream( @Nonnull final Subscription upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  /**
   * Return the subscription used to interact with the upstream stage.
   * This method should not be invoked except when the subscription is
   * known to be set and an invariant failure will be generated in development
   * mode if upstream not set.
   *
   * @return the subscription used to interact with the upstream stage.
   */
  @Nonnull
  protected final Subscription getUpstream()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      Guards.invariant( () -> null != _upstream,
                        () -> "Spritz-0002: Attempted to invoke getUpstream() when subscription is not present" );
    }
    assert null != _upstream;
    return _upstream;
  }

  /**
   * Create the subscription with the downstream subscriber.
   *
   * @param downstreamSubscriber the downstream subscriber.
   */
  AbstractOperatorSubscription( @Nonnull final Subscriber<? super DownstreamT> downstreamSubscriber )
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
  public void onError( @Nonnull final Throwable error )
  {
    markAsDone();
    getDownstreamSubscriber().onError( error );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onComplete()
  {
    markAsDone();
    getDownstreamSubscriber().onComplete();
  }

  void doCancel()
  {
    getUpstream().cancel();
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
