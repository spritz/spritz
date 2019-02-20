package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class AbstractThrottlingSubscription<T>
  extends PassThroughSubscription<T>
  implements Runnable
{
  @Nullable
  private T _nextItem;
  @Nullable
  private Cancelable _task;
  private int _nextTaskTime;
  private boolean _pendingComplete;

  AbstractThrottlingSubscription( @Nonnull final Subscriber<? super T> subscriber )
  {
    super( subscriber );
  }

  @Override
  public final void onNext( @Nonnull final T item )
  {
    final int now = Scheduler.now();

    /*
     * Sometimes the schedulers are lagging behind and thus we check to see if there is an item
     * pending that we have expected to emitted and if so emit the item before performing normal
     * onNext action.
     */
    if ( hasNextItem() && now > _nextTaskTime )
    {
      assert null != _task;
      _task.cancel();
      executeTask();
    }

    doOnNext( now, item );
  }

  protected abstract void doOnNext( final int now, @Nonnull final T item );

  @Override
  public final void onError( @Nonnull final Throwable error )
  {
    cancelPendingTask();
    super.onError( error );
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
    cancelPendingTask();
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

  void executeTask()
  {
    assert null != _nextItem;
    assert null != _task;
    super.onNext( _nextItem );
    _nextItem = null;
    _task = null;
    _nextTaskTime = 0;
    if ( _pendingComplete )
    {
      doOnComplete();
    }
  }

  final void scheduleTaskForItem( @Nonnull final T item, final int timeout )
  {
    assert timeout >= 0;
    if ( 0 == timeout )
    {
      super.onNext( item );
      setNextItem( null );
    }
    else
    {
      setNextItem( item );
      scheduleTask( timeout );
    }
  }

  final void scheduleTask( final int delay )
  {
    assert delay > 0;
    _task = Scheduler.schedule( this, delay );
    _nextTaskTime = Scheduler.now() + delay;
  }

  /**
   * Cleanup pending task if any.
   */
  final void cancelPendingTask()
  {
    if ( null != _task )
    {
      _task.cancel();
      assert null != _nextItem;
      _task = null;
      _nextTaskTime = 0;
      _nextItem = null;
    }
    else
    {
      assert null == _nextItem;
    }
  }
}
