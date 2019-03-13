package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class SampleOperator<T>
  extends AbstractStream<T, T>
{
  private final int _samplePeriod;
  private final boolean _emitFirst;

  SampleOperator( @Nullable final String name,
                  @Nonnull final Stream<T> upstream,
                  final int samplePeriod,
                  final boolean emitFirst )
  {
    super( Spritz.areNamesEnabled() ?
           generateName( name, "sample", "samplePeriod=" + samplePeriod + ",emitFirst=" + emitFirst ) :
           null,
           upstream );
    _samplePeriod = samplePeriod;
    _emitFirst = emitFirst;
    assert samplePeriod > 0;
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
    extends AbstractThrottlingSubscription<T, SampleOperator<T>>
  {
    /**
     * The next time that the subscription can emit an item.
     * If no item is emitted before next sample time then the sampling starts again.
     * 0 indicates sample period has not started while any other value indicates the
     * time at which sample should be completed.
     */
    private int _nextSampleTime;

    WorkerSubscription( @Nonnull final SampleOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
      _nextSampleTime = 0;
    }

    @Override
    void doOnNext( final int now, @Nonnull final T item )
    {
      if ( now > _nextSampleTime )
      {
        /*
         * If we are beyond the sample period then that implies we went a whole sample
         * period with out emitting any values from upstream and thus the sampling needs
         * to start again.
         */
        assert !hasNextItem();
        _nextSampleTime = now + getStream()._samplePeriod;
        if ( getStream()._emitFirst )
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
      _nextSampleTime = _nextSampleTime + getStream()._samplePeriod;
      super.executeTask();
    }
  }
}
