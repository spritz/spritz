package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import zemeckis.Cancelable;
import zemeckis.Scheduler;
import zemeckis.Zemeckis;

final class PeriodicStreamSource
  extends Stream<Integer>
{
  private final int _period;

  PeriodicStreamSource( @Nullable final String name, final int period )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "periodic", String.valueOf( period ) ) : null );
    assert period >= 0;
    _period = period;
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super Integer> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.startTimer();
    return subscription;
  }

  private static final class WorkerSubscription
    extends AbstractStreamSubscription<Integer, PeriodicStreamSource>
    implements Runnable
  {
    private int _counter;
    @Nullable
    private Cancelable _task;

    WorkerSubscription( @Nonnull final PeriodicStreamSource stream,
                        @Nonnull final Subscriber<? super Integer> subscriber )
    {
      super( stream, subscriber );
    }

    synchronized void startTimer()
    {
      _task = Scheduler.scheduleAtFixedRate( this, getStream()._period );
    }

    @Override
    public void run()
    {
      Scheduler.becomeMacroTask( this::pushItem );
    }

    synchronized void pushItem()
    {
      assert null != _task;
      final int value = _counter++;
      try
      {
        getSubscriber().onNext( value );
      }
      catch ( final Throwable t )
      {
        Zemeckis.reportUncaughtError( t );
      }
    }

    @Override
    synchronized void doCancel()
    {
      if ( null != _task )
      {
        _task.cancel();
        _task = null;
      }
    }
  }
}
