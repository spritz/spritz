package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super Integer> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.startTimer();
  }

  private static final class WorkerSubscription
    extends AbstractSubscription<Integer, PeriodicStreamSource>
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
      _task = Scheduler.scheduleAtFixedRate( this::pushItem, getStream()._period );
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
        Spritz.reportUncaughtError( t );
      }
    }

    @Override
    protected synchronized void doCancel()
    {
      if ( null != _task )
      {
        _task.cancel();
        _task = null;
      }
    }
  }
}
