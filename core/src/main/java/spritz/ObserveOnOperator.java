package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class ObserveOnOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final VirtualProcessorUnit _virtualProcessorUnit;

  ObserveOnOperator( @Nullable final String name,
                     @Nonnull final Stream<T> upstream,
                     @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "observeOn", virtualProcessorUnit.getName() ) : null,
           upstream );
    _virtualProcessorUnit = Objects.requireNonNull( virtualProcessorUnit );
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
    extends PassThroughSubscription<T, ObserveOnOperator<T>>
  {
    static final int INITIAL_CAPACITY = 10;
    @Nullable
    private Subscription _subscription;
    @Nullable
    private CircularBuffer<T> _buffer;
    @Nullable
    private Throwable _error;
    private boolean _complete;

    WorkerSubscription( @Nonnull final ObserveOnOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      _subscription = subscription;
      scheduleObserve();
    }

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

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      _error = error;
      scheduleObserve();
    }

    @Override
    public void onComplete()
    {
      _complete = true;
      scheduleObserve();
    }

    private void scheduleObserve()
    {
      getStream()._virtualProcessorUnit.getExecutor().queue( this::observe );
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
