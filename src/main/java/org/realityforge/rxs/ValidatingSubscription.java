package org.realityforge.rxs;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.fail;
import static org.realityforge.braincheck.Guards.invariant;

public final class ValidatingSubscription
  implements Flow.Subscription
{
  @Nonnull
  private final ValidatingSubscriber<?> _subscriber;
  @Nonnull
  private final Flow.Subscription _subscription;
  private boolean _cancelled;

  public ValidatingSubscription( @Nonnull final ValidatingSubscriber<?> subscriber,
                                 @Nonnull final Flow.Subscription subscription )
  {
    _subscriber = Objects.requireNonNull( subscriber );
    _subscription = Objects.requireNonNull( subscription );
  }

  @Override
  public void request( final int count )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( ValidatingSubscriber::hasContext,
                 () -> "Rxs-0013: Invoking Subscription.request(...) but not in the context of a subscriber." );
      final ValidatingSubscriber<?> subscriber = ValidatingSubscriber.currentContext();
      invariant( () -> subscriber == _subscriber,
                 () -> "Rxs-0014: Invoking Subscription.request(...) in the context of subscriber '" + subscriber +
                       "' but expected to be in the context of subscriber '" + _subscriber + "'." );
      invariant( () -> ValidatingSubscriber.State.SUBSCRIBED == _subscriber.getState(),
                 () -> "Rxs-0015: Invoking Subscription.request(...) when the subscriber '" + subscriber +
                       "' is not in the expected SUBSCRIBED state." );
    }
    if ( !_cancelled )
    {
      try
      {
        _subscription.request( count );
      }
      catch ( final Throwable t )
      {
        if ( BrainCheckConfig.checkInvariants() )
        {
          fail( () -> "Rxs-0017: Invoking Subscription.request(...) incorrectly threw an exception. " +
                      "Exception:\n" + ErrorUtil.throwableToString( t ) );
        }
        throw t;
      }
    }
  }

  @Override
  public void cancel()
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( ValidatingSubscriber::hasContext,
                 () -> "Rxs-0018: Invoking Subscription.cancel(...) but not in the context of a subscriber." );
      final ValidatingSubscriber<?> subscriber = ValidatingSubscriber.currentContext();
      invariant( () -> subscriber == _subscriber,
                 () -> "Rxs-0019: Invoking Subscription.cancel(...) in the context of subscriber '" + subscriber +
                       "' but expected to be in the context of subscriber '" + _subscriber + "'." );
    }
    if ( !_cancelled )
    {
      try
      {
        _cancelled = true;
        _subscription.cancel();
      }
      catch ( final Throwable t )
      {
        if ( BrainCheckConfig.checkInvariants() )
        {
          fail( () -> "Rxs-0020: Invoking Subscription.cancel(...) incorrectly threw an exception. " +
                      "Exception:\n" + ErrorUtil.throwableToString( t ) );
        }
        throw t;
      }
    }
  }
}
