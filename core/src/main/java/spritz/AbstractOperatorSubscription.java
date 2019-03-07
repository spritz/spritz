package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.Guards;

/**
 * Abstract implementation for subscription with both an upstream and downstream stream stage.
 */
abstract class AbstractOperatorSubscription<UpstreamT, DownstreamT, StreamT extends Stream<DownstreamT>>
  extends AbstractSubscription<DownstreamT, StreamT>
  implements Subscriber<UpstreamT>
{
  /**
   * The upstream subscription.
   */
  @Nullable
  private Subscription _upstream;

  /**
   * Create the subscription for the specified stream and specified subscriber.
   *
   * @param stream     the stream.
   * @param subscriber the subscriber.
   */
  AbstractOperatorSubscription( @Nonnull final StreamT stream,
                                @Nonnull final Subscriber<? super DownstreamT> subscriber )
  {
    super( stream, subscriber );
  }

  /**
   * Set the upstream subscription.
   * This method is expected to be invoked as the first part of the {@link Subscriber#onSubscribe(Subscription)}
   * step.
   *
   * @param upstream the upstream subscription.
   */
  final void setUpstream( @Nonnull final Subscription upstream )
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
  final Subscription getUpstream()
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
   * {@inheritDoc}
   */
  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    setUpstream( subscription );
    getSubscriber().onSubscribe( this );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onError( @Nonnull final Throwable error )
  {
    markAsCancelled();
    getSubscriber().onError( error );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onComplete()
  {
    markAsCancelled();
    getSubscriber().onComplete();
  }

  void doCancel()
  {
    getUpstream().cancel();
  }
}
