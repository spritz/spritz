package streak;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.schedulers.Schedulers;
import streak.schedulers.Task;

abstract class AbstractThrottlingSubscription<T>
  extends AbstractOperatorSubscription<T>
  implements Runnable
{
  @Nullable
  private T _nextItem;
  @Nullable
  private Task _task;
  private boolean _pendingComplete;

  AbstractThrottlingSubscription( @Nonnull final Subscriber<? super T> subscriber )
  {
    super( subscriber );
  }

  @Override
  public final void onError( @Nonnull final Throwable throwable )
  {
    clearPendingTask();
    super.onError( throwable );
  }

  @Override
  public final void onComplete()
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
  public final void run()
  {
    executeTask();
  }

  final boolean hasTask()
  {
    return null != _task;
  }

  final boolean hasNextItem()
  {
    return null != _nextItem;
  }

  final void setNextItem( @Nullable final T nextItem )
  {
    _nextItem = nextItem;
  }

  final void cancelAndRunTask()
  {
    assert null != _task;
    _task.cancel();
    executeTask();
  }

  private void executeTask()
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

  final void scheduleTask( final int delay )
  {
    assert delay > 0;
    _task = Schedulers.current().schedule( this, delay );
  }

  /**
   * Cleanup pending task if any.
   */
  final void clearPendingTask()
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
