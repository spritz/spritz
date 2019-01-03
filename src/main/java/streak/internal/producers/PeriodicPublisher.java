package streak.internal.producers;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class PeriodicPublisher
  extends AbstractStream<Integer>
{
  private final int _period;

  PeriodicPublisher( final int period )
  {
    assert period >= 0;
    _period = period;
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super Integer> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription( subscriber, _period );
    subscriber.onSubscribe( subscription );
    subscription.startTimer();
  }

  private static final class WorkerSubscription
    implements Flow.Subscription
  {
    private final Flow.Subscriber<? super Integer> _subscriber;
    private final int _period;
    private int _counter;
    private Timer _timer;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super Integer> subscriber, final int period )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _period = period;
      _timer = new Timer();
    }

    void startTimer()
    {
      _timer.schedule( new TimerTask()
      {
        @Override
        public void run()
        {
          pushItem();
        }
      }, 0, _period );
    }

    void pushItem()
    {
      if ( isNotDisposed() )
      {
        final int value = _counter++;
        _subscriber.onNext( value );
      }
    }

    @Override
    public void dispose()
    {
      if ( null != _timer )
      {
        _timer.cancel();
        _timer = null;
      }
    }

    @Override
    public boolean isDisposed()
    {
      return null == _timer;
    }
  }
}
