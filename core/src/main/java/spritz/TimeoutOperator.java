package spritz;

import javax.annotation.Nonnull;

final class TimeoutOperator<T>
  extends AbstractStream<T>
{
  private final int _timeoutTime;

  TimeoutOperator( @Nonnull final Stream<? extends T> upstream, final int timeoutTime )
  {
    super( upstream );
    _timeoutTime = timeoutTime;
    assert timeoutTime > 0;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _timeoutTime ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<T>
    implements Runnable
  {
    private final int _timeoutTime;
    private int _lastTime;
    @Nonnull
    private Scheduler.Task _task;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, final int timeoutTime )
    {
      super( subscriber );
      _timeoutTime = timeoutTime;
      recordLastTime();
      _task = scheduleTimeout();
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      recordLastTime();
      _task.cancel();
      _task = scheduleTimeout();
      super.onNext( item );
    }

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      _task.cancel();
      super.onError( error );
    }

    @Override
    public void onComplete()
    {
      _task.cancel();
      super.onComplete();
    }

    @Override
    public void run()
    {
      onError( new TimeoutException() );
    }

    private void recordLastTime()
    {
      _lastTime = Scheduler.instance().now();
    }

    @Nonnull
    private Scheduler.Task scheduleTimeout()
    {
      return Scheduler.instance().schedule( this, _lastTime + _timeoutTime );
    }
  }
}
