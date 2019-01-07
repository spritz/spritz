package streak;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import org.realityforge.braincheck.Guards;

abstract class AbstractChainedSubscription
  implements Subscription
{
  @Nullable
  private Subscription _upstream;

  protected final void setUpstream( @Nonnull final Subscription upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  /**
   * Return the subscription used to interact with the upstream publisher.
   *
   * @return the subscription used to interact with the upstream publisher.
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
