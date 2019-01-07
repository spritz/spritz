package streak;

import arez.Disposable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.schedulers.Schedulers;

final class ThrottleLatestOperator<T>
  extends StreamWithUpstream<T>
{
  private final int _throttleTime;

  ThrottleLatestOperator( @Nonnull final Stream<? extends T> upstream, final int throttleTime )
  {
    super( upstream );
    _throttleTime = throttleTime;
    assert throttleTime > 0;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _throttleTime ) );
  }

  private static final class WorkerSubscription<T>
    extends SubscriptionWithDownstream<T>
    implements Runnable
  {
    private final int _throttleTime;
    /**
     * indicates the next time that the subscription will emit an item.
     * 0 indicates no item scheduled to be emitted.
     * any other value indicates the time at which item should be emitted.
     */
    private int _nextTime;
    @Nullable
    private T _nextItem;
    @Nullable
    private Disposable _task;
    private boolean _pendingComplete;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, final int throttleTime )
    {
      super( subscriber );
      _throttleTime = throttleTime;
      _nextTime = -1;
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      final int now = Schedulers.current().now();
      if ( null != _nextItem && now > _nextTime )
      {
        assert null != _task;
        _task.dispose();
        // This may update nextTime
        runScheduledTask();
      }

      // _nextTime may have been updated above
      if ( now > _nextTime )
      {
        _nextTime = now + _throttleTime;
        super.onNext( item );
      }
      else
      {
        _nextItem = item;
        if ( null == _task )
        {
          _task = Schedulers.current().schedule( this, _nextTime - now );
        }
      }
    }

    @Override
    public void onError( @Nonnull final Throwable throwable )
    {
      clearPendingTask();
      super.onError( throwable );
    }

    @Override
    public void onComplete()
    {
      if ( null == _nextItem )
      {
        doOnComplete();
      }
      else
      {
        _pendingComplete = true;
      }
    }

    private void doOnComplete()
    {
      clearPendingTask();
      super.onComplete();
    }

    @Override
    public void run()
    {
      runScheduledTask();
    }

    private void runScheduledTask()
    {
      assert null != _nextItem;
      assert null != _task;
      super.onNext( _nextItem );
      _nextItem = null;
      _nextTime = _nextTime + _throttleTime;
      _task = null;
      if ( _pendingComplete )
      {
        doOnComplete();
      }
    }

    /**
     * Cleanup pending task if any.
     */
    private void clearPendingTask()
    {
      if ( null != _task )
      {
        _task.dispose();
        assert null != _nextItem;
        _task = null;
        _nextItem = null;
      }
      else
      {
        assert null == _nextItem;
      }
    }
  }
}
