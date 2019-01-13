package streak;

import javax.annotation.Nonnull;

final class SampleOperator<T>
  extends AbstractStream<T>
{
  private final int _samplePeriod;
  private final boolean _emitFirst;

  SampleOperator( @Nonnull final Stream<? extends T> upstream, final int samplePeriod, final boolean emitFirst )
  {
    super( upstream );
    _samplePeriod = samplePeriod;
    _emitFirst = emitFirst;
    assert samplePeriod > 0;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _samplePeriod, _emitFirst ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractThrottlingSubscription<T>
  {
    private final int _samplePeriod;
    private final boolean _emitFirst;
    /**
     * The next time that the subscription can emit an item.
     * If no item is emitted before next sample time then the sampling starts again.
     * 0 indicates sample period has not started while any other value indicates the
     * time at which sample should be completed.
     */
    private int _nextSampleTime;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        final int samplePeriod,
                        final boolean emitFirst )
    {
      super( subscriber );
      _samplePeriod = samplePeriod;
      _emitFirst = emitFirst;
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
        if ( _emitFirst )
        {
          // Emit here and return immediately so don't schedule emit.
          super.onNext( item );
          return;
        }
      }

      setNextItem( item );
      if ( !hasTask() )
      {
        scheduleTask( _nextSampleTime - now );
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
