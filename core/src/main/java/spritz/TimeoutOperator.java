package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class TimeoutOperator<T>
  extends AbstractStream<T, T>
{
  private final int _timeoutTime;

  TimeoutOperator( @Nullable final String name, @Nonnull final Stream<T> upstream, final int timeoutTime )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "timeout", String.valueOf( timeoutTime ) ) : null, upstream );
    _timeoutTime = timeoutTime;
    assert timeoutTime > 0;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends PassThroughSubscription<T, TimeoutOperator<T>>
    implements Runnable
  {
    private int _lastTime;
    @Nonnull
    private Cancelable _task;

    WorkerSubscription( @Nonnull final TimeoutOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
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
      Scheduler.runMacroTaskNow( () -> super.onError( new TimeoutException() ) );
    }

    private void recordLastTime()
    {
      _lastTime = Scheduler.now();
    }

    @Nonnull
    private Cancelable scheduleTimeout()
    {
      return Scheduler.schedule( this, _lastTime + getStream()._timeoutTime );
    }
  }
}
