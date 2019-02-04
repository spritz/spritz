package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.schedulers.SchedulerTask;

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
    private SchedulerTask _task;

    WorkerSubscription( @Nonnull final Subscriber<? super Integer> subscriber, final int period )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _period = period;
    }

    void startTimer()
    {
      _task = Spritz.scheduler().scheduleAtFixedRate( this::pushItem, _period );
    }

    void pushItem()
    {
      assert null != _task;
      final int value = _counter++;
      _subscriber.onNext( value );
    }

    @Override
    public void cancel()
    {
      if ( null != _task )
      {
        _task.cancel();
        _task = null;
      }
    }
  }
}
