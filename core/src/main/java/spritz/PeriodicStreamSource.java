package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class PeriodicStreamSource
  extends Stream<Integer>
{
  private final int _period;

  PeriodicStreamSource( final int period )
  {
    assert period >= 0;
    _period = period;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super Integer> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription( subscriber, _period );
    subscriber.onSubscribe( subscription );
    subscription.startTimer();
  }

  private static final class WorkerSubscription
    implements Subscription
  {
    private final Subscriber<? super Integer> _subscriber;
    private final int _period;
    private int _counter;
    @Nullable
    private Cancelable _task;

    WorkerSubscription( @Nonnull final Subscriber<? super Integer> subscriber, final int period )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _period = period;
    }

    synchronized void startTimer()
    {
      _task = Scheduler.scheduleAtFixedRate( this::pushItem, _period );
    }

    synchronized void pushItem()
    {
      assert null != _task;
      final int value = _counter++;
      try
      {
        _subscriber.onNext( value );
      }
      catch ( final Throwable t )
      {
        Spritz.reportUncaughtError( t );
      }
    }

    @Override
    public synchronized void cancel()
    {
      if ( null != _task )
      {
        _task.cancel();
        _task = null;
      }
    }
  }
}
