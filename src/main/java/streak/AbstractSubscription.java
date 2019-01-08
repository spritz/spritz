package streak;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import org.realityforge.braincheck.Guards;

/**
 * Abstract subscription implementation for the common scenario where
 * there is an upstream stage and associated subscription.
 */
abstract class AbstractSubscription
  implements Subscription
{
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
    if ( BrainCheckConfig.checkInvariants() )
    {
      Guards.invariant( () -> null != _upstream,
                        () -> "Streak-0002: Attempted to invoke getUpstream() when subscription is not present" );
    }
    assert null != _upstream;
    return _upstream;
  }
}
