package streak;

import arez.Disposable;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.schedulers.Schedulers;

final class PeriodicStreamSource
  extends AbstractStream<Integer>
{
  private final int _period;

  PeriodicStreamSource( final int period )
  {
    assert period >= 0;
    _period = period;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super Integer> subscriber )
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
    private Disposable _task;

    WorkerSubscription( @Nonnull final Subscriber<? super Integer> subscriber, final int period )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _period = period;
    }

    void startTimer()
    {
      _task = Schedulers.current().scheduleAtFixedRate( this::pushItem, _period );
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
      if ( null != _task )
      {
        _task.dispose();
        _task = null;
      }
    }

    @Override
    public boolean isDisposed()
    {
      return null == _task;
    }
  }
}
