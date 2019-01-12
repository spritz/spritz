package streak;

import javax.annotation.Nonnull;
import streak.schedulers.Schedulers;

final class ThrottleLatestOperator<T>
  extends AbstractStream<T>
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
    extends AbstractThrottlingSubscription<T>
  {
    private final int _throttleTime;
    /**
     * indicates the next time that the subscription will emit an item.
     * 0 indicates no item scheduled to be emitted.
     * any other value indicates the time at which item should be emitted.
     */
    private int _nextTime;

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
      if ( hasNextItem() && now > _nextTime )
      {
        _nextTime = now + _throttleTime;
        cancelAndRunTask();
      }

      // _nextTime may have been updated above
      if ( now > _nextTime )
      {
        _nextTime = now + _throttleTime;
        super.onNext( item );
      }
      else
      {
        setNextItem( item );
        if ( !hasTask() )
        {
          scheduleTask( _nextTime - now );
        }
      }
    }
  }
}
