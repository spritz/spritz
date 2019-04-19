package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import static org.realityforge.braincheck.Guards.*;

final class ValidatingSubscriber<T>
  implements Subscriber<T>
{
  enum State
  {
    CREATED,
    SUBSCRIBE_STARTED,
    SUBSCRIBE_COMPLETED,
    ERRORED,
    COMPLETED
  }

  @Nonnull
  private final Subscriber<T> _target;
  @Nonnull
  private State _state;

  ValidatingSubscriber( @Nonnull final Subscriber<T> target )
  {
    _target = Objects.requireNonNull( target );
    _state = State.CREATED;
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.CREATED == _state,
                 () -> "Spritz-0001: Subscriber.onSubscribe(...) called and expected state " +
                       "to be CREATED but is " + _state );
    }
    try
    {
      _state = State.SUBSCRIBE_STARTED;
      _target.onSubscribe( new WorkerSubscription( subscription ) );
      _state = State.SUBSCRIBE_COMPLETED;
    }
    catch ( final Throwable throwable )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0003: Invoking Subscriber.onSubscribe(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( throwable ) );
      }
      throw throwable;
    }
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.SUBSCRIBE_COMPLETED == _state,
                 () -> "Spritz-0005: Subscriber.onNext(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
      Objects.requireNonNull( item );
    }

    try
    {
      _target.onNext( item );
    }
    catch ( final Throwable throwable )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0004: Invoking Subscriber.onNext(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( throwable ) );
      }
      throw throwable;
    }
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.SUBSCRIBE_COMPLETED == _state,
                 () -> "Spritz-0006: Subscriber.onError(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
      Objects.requireNonNull( error );
    }
    try
    {
      _state = State.ERRORED;
      _target.onError( error );
    }
    catch ( final Throwable t )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0007: Invoking Subscriber.onError(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( t ) );
      }
      throw t;
    }
  }

  @Override
  public void onComplete()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( () -> State.SUBSCRIBE_COMPLETED == _state,
                 () -> "Spritz-0008: Subscriber.onComplete(...) called and expected state " +
                       "to be SUBSCRIBED but is " + _state );
    }

    try
    {
      _state = State.COMPLETED;
      _target.onComplete();
    }
    catch ( final Throwable t )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        fail( () -> "Spritz-0009: Invoking Subscriber.onComplete(...) incorrectly threw an exception. " +
                    "Exception:\n" + ErrorUtil.throwableToString( t ) );
      }
      throw t;
    }
  }

  private static final class WorkerSubscription
    extends Subscription
  {
    @Nonnull
    private final Subscription _subscription;

    WorkerSubscription( @Nonnull final Subscription subscription )
    {
      _subscription = Objects.requireNonNull( subscription );
    }

    @Override
    void doCancel()
    {
      try
      {
        _subscription.cancel();
      }
      catch ( final Throwable t )
      {
        if ( Spritz.shouldCheckInvariants() )
        {
          fail( () -> "Spritz-0020: Invoking Subscription.cancel(...) incorrectly threw an exception. " +
                      "Exception:\n" + ErrorUtil.throwableToString( t ) );
        }
        throw t;
      }
    }

    @Override
    String getQualifiedName()
    {
      return _subscription.getQualifiedName();
    }
  }
}
