package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import zemeckis.Cancelable;
import zemeckis.Zemeckis;

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

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    getUpstream().subscribe( subscription );
    return subscription;
  }

  private static final class WorkerSubscription<T>
    extends PassThroughSubscription<T, TimeoutOperator<T>>
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
    public void onItem( @Nonnull final T item )
    {
      recordLastTime();
      _task.cancel();
      _task = scheduleTimeout();
      super.onItem( item );
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

    private void recordLastTime()
    {
      _lastTime = Zemeckis.now();
    }

    @Nonnull
    private Cancelable scheduleTimeout()
    {
      return Zemeckis.delayedTask( Spritz.areNamesEnabled() ? getStream().getName() : null,
                                   () -> super.onError( new TimeoutException() ),
                                   _lastTime + getStream()._timeoutTime );
    }
  }
}
