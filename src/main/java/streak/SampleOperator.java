package streak;

import javax.annotation.Nonnull;

final class SampleOperator<T>
  extends AbstractStream<T>
{
  private final int _samplePeriod;

  SampleOperator( @Nonnull final Stream<? extends T> upstream, final int samplePeriod )
  {
    super( upstream );
    _samplePeriod = samplePeriod;
    assert samplePeriod > 0;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _samplePeriod ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractThrottlingSubscription<T>
  {
    private final int _samplePeriod;
    /**
     * The next time that the subscription can emit an item.
     * If no item is emitted before next sample time then the sampling starts again.
     * 0 indicates sample period has not started while any other value indicates the
     * time at which sample should be completed.
     */
    private int _nextSampleTime;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, final int samplePeriod )
    {
      super( subscriber );
      _samplePeriod = samplePeriod;
      _nextSampleTime = 0;
    }

    @Override
    protected void doOnNext( final int now, @Nonnull final T item )
    {
      if ( now > _nextSampleTime )
      {
        /*
         * If we are beyond the sample period then that implies we went a whole sample
         * period with out emitting any values from upstream and thus the sampling needs
         * to start again.
         */
        assert !hasNextItem();
        _nextSampleTime = now + _samplePeriod;
        //TODO: Add ability to not emit first but instead schedule emit
        super.onNext( item );
      }
      else
      {
        setNextItem( item );
        if ( !hasTask() )
        {
          scheduleTask( _nextSampleTime - now );
        }
      }
    }

    @Override
    void executeTask()
    {
      _nextSampleTime = _nextSampleTime + _samplePeriod;
      super.executeTask();
    }
  }
}
