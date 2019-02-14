package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.internal.util.CircularBuffer;

final class ObserveOnOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final VirtualProcessorUnit _virtualProcessorUnit;

  ObserveOnOperator( @Nonnull final Stream<? extends T> upstream,
                     @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    super( upstream );
    _virtualProcessorUnit = Objects.requireNonNull( virtualProcessorUnit );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _virtualProcessorUnit ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<T>
  {
    static final int INITIAL_CAPACITY = 10;
    @Nonnull
    private final VirtualProcessorUnit _virtualProcessorUnit;
    @Nullable
    private Subscription _subscription;
    @Nullable
    private CircularBuffer<T> _buffer;
    @Nullable
    private Throwable _error;
    private boolean _complete;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
    {
      super( subscriber );
      _virtualProcessorUnit = virtualProcessorUnit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      _subscription = subscription;
      scheduleObserve();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext( @Nonnull final T item )
    {
      if ( null == _buffer )
      {
        _buffer = new CircularBuffer<>( INITIAL_CAPACITY );
      }
      _buffer.add( item );
      scheduleObserve();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError( @Nonnull final Throwable error )
    {
      _error = error;
      scheduleObserve();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete()
    {
      _complete = true;
      scheduleObserve();
    }

    private void scheduleObserve()
    {
      _virtualProcessorUnit.queue( this::observe );
    }

    /**
     * The method responsible for emitting items and signals.
     * Must be invoked in the context of VPU.
     */
    private void observe()
    {
      if ( null != _subscription )
      {
        super.onSubscribe( _subscription );
      }
      if ( null != _buffer )
      {
        T item;
        while ( null != ( item = _buffer.pop() ) )
        {
          super.onNext( item );
        }
      }
      if ( null != _error )
      {
        super.onError( _error );
      }
      else if ( _complete )
      {
        super.onComplete();
      }
    }
  }
}
