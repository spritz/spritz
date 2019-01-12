package streak;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.schedulers.Schedulers;
import streak.schedulers.Task;

final class ThrottleWithTimeoutOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final TimeoutForItemFn<T> _timeoutForItemFn;

  ThrottleWithTimeoutOperator( @Nonnull final Stream<? extends T> upstream,
                               @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    super( upstream );
    _timeoutForItemFn = timeoutForItemFn;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _timeoutForItemFn ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<T>
    implements Runnable
  {
    @Nonnull
    private final TimeoutForItemFn<T> _timeoutForItemFn;
    @Nullable
    private T _nextItem;
    @Nullable
    private Task _task;
    private boolean _pendingComplete;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
    {
      super( subscriber );
      _timeoutForItemFn = timeoutForItemFn;
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      final int now = Schedulers.current().now();
      if ( null != _task )
      {
        _task.cancel();
      }
      _nextItem = item;
      final int timeout = _timeoutForItemFn.getTimeout( item );
      assert timeout >= 0;
      if ( 0 == timeout )
      {
        super.onNext( _nextItem );
        _nextItem = null;
        _task = null;
      }
      else
      {
        _task = Schedulers.current().schedule( this, now + timeout );
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
        _task.cancel();
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
