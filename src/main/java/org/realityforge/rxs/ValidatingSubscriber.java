package org.realityforge.rxs;

import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.fail;
import static org.realityforge.braincheck.Guards.invariant;

@SuppressWarnings( "ConstantConditions" )
public final class ValidatingSubscriber<T>
  implements Flow.Subscriber<T>
{
  private static final ArrayList<ValidatingSubscriber<?>> c_subscriberContext = new ArrayList<>();

  enum State
  {
    CREATED,
    SUBSCRIBED,
    ERRORED,
    COMPLETED
  }

  @Nonnull
  private final Flow.Subscriber<T> _target;
  @Nonnull
  private State _state;

  public ValidatingSubscriber( @Nonnull final Flow.Subscriber<T> target )
  {
    _target = Objects.requireNonNull( target );
    _state = State.CREATED;
  }

  @Override
  public void onSubscribe( @Nonnull final Flow.Subscription subscription )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> State.CREATED == _state,
                 () -> "Rxs-0001: Subscriber.onSubscribe(...) called and expected state " +
                       "to be CREATED but is " + _state );
      if ( null == subscription )
      {
        // This is in a guard so it is optimized out in production code
        throw new NullPointerException();
      }
    }
    try
    {
      pushContext( this );
      _state = State.SUBSCRIBED;
      _target.onSubscribe( new WorkerSubscription<T>( this, subscription ) );
    }
    catch ( final Throwable throwable )
    {
      if ( BrainCheckConfig.checkInvariants() )
      {
        fail( () -> "Rxs-0003: Invoking Subscriber.onSubscribe(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( throwable ) );
      }
      throw throwable;
    }
    finally
    {
      popContext( this );
    }
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> State.SUBSCRIBED == _state,
                 () -> "Rxs-0005: Subscriber.onNext(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
      if ( null == item )
      {
        // This is in a guard so it is optimized out in production code
        throw new NullPointerException();
      }
    }

    try
    {
      pushContext( this );
      _target.onNext( item );
    }
    catch ( final Throwable throwable )
    {
      if ( BrainCheckConfig.checkInvariants() )
      {
        fail( () -> "Rxs-0004: Invoking Subscriber.onNext(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( throwable ) );
      }
      throw throwable;
    }
    finally
    {
      popContext( this );
    }
  }

  @Override
  public void onError( @Nonnull final Throwable throwable )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> State.SUBSCRIBED == _state,
                 () -> "Rxs-0006: Subscriber.onError(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
      if ( null == throwable )
      {
        // This is in a guard so it is optimized out in production code
        throw new NullPointerException();
      }
    }
    try
    {
      pushContext( this );
      _state = State.ERRORED;
      _target.onError( throwable );
    }
    catch ( final Throwable t )
    {
      if ( BrainCheckConfig.checkInvariants() )
      {
        fail( () -> "Rxs-0007: Invoking Subscriber.onError(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( t ) );
      }
      throw t;
    }
    finally
    {
      popContext( this );
    }
  }

  @Override
  public void onComplete()
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> State.SUBSCRIBED == _state,
                 () -> "Rxs-0008: Subscriber.onComplete(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
    }

    try
    {
      pushContext( this );
      _state = State.COMPLETED;
      _target.onComplete();
    }
    catch ( final Throwable t )
    {
      if ( BrainCheckConfig.checkInvariants() )
      {
        fail( () -> "Rxs-0009: Invoking Subscriber.onComplete(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( t ) );
      }
      throw t;
    }
    finally
    {
      popContext( this );
    }
  }

  @Nonnull
  private State getState()
  {
    return _state;
  }

  private static boolean hasContext()
  {
    return !c_subscriberContext.isEmpty();
  }

  @Nonnull
  private static ValidatingSubscriber<?> currentContext()
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( ValidatingSubscriber::hasContext,
                 () -> "Rxs-0012: Invoking Subscriber.currentContext(...) but no subscriber on stack." );
    }
    final int index = c_subscriberContext.size() - 1;
    return c_subscriberContext.remove( index );
  }

  private static void pushContext( @Nonnull final ValidatingSubscriber<?> subscriber )
  {
    c_subscriberContext.add( Objects.requireNonNull( subscriber ) );
  }

  private static void popContext( @Nonnull final ValidatingSubscriber<?> subscriber )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> !c_subscriberContext.isEmpty(),
                 () -> "Rxs-0010: Invoking Subscriber.popContext(...) but no subscriber on stack. " +
                       "Expecting subscriber: " + subscriber );
    }
    final int index = c_subscriberContext.size() - 1;
    final ValidatingSubscriber<?> removed = c_subscriberContext.remove( index );
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> removed == subscriber,
                 () -> "Rxs-0011: Invoking Subscriber.popContext(...) popped subscriber '" + removed +
                       "' but was expecting subscriber '" + subscriber + "'." );
    }
  }

  private static final class WorkerSubscription<T>
    implements Flow.Subscription
  {
    @Nonnull
    private final ValidatingSubscriber<T> _subscriber;
    @Nonnull
    private final Flow.Subscription _subscription;
    private boolean _cancelled;

    WorkerSubscription( @Nonnull final ValidatingSubscriber<T> subscriber, @Nonnull final Flow.Subscription subscription )
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
        final ValidatingSubscriber<?> subscriber = currentContext();
        invariant( () -> subscriber == _subscriber,
                   () -> "Rxs-0014: Invoking Subscription.request(...) in the context of subscriber '" + subscriber +
                         "' but expected to be in the context of subscriber '" + _subscriber + "'." );
        invariant( () -> State.SUBSCRIBED == _subscriber.getState(),
                   () -> "Rxs-0015: Invoking Subscription.request(...) when the subscriber '" + subscriber +
                         "' is not in the expected SUBSCRIBED state." );
        invariant( () -> count > 0,
                   () -> "Rxs-0021: Invoking Subscription.request(...) with count " +
                         count + " but count must be a positive number." );
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
        final ValidatingSubscriber<?> subscriber = currentContext();
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
}
