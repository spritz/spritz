package streak.internal.filtering;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.StreamWithUpstream;
import streak.internal.SubscriptionWithDownstream;
import streak.schedulers.CircularBuffer;

final class LastOperator<T>
  extends StreamWithUpstream<T>
{
  private final int _maxBufferSize;

  LastOperator( @Nonnull final Flow.Stream<? extends T> upstream, final int maxBufferSize )
  {
    super( upstream );
    _maxBufferSize = maxBufferSize;
    assert maxBufferSize > 0;
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _maxBufferSize ) );
  }

  private static final class WorkerSubscription<T>
    extends SubscriptionWithDownstream<T>
  {
    @Nonnull
    private final CircularBuffer<T> _buffer;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, final int maxBufferSize )
    {
      super( subscriber );
      _buffer = new CircularBuffer<>( maxBufferSize );
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      if ( _buffer.size() == _buffer.getCapacity() )
      {
        _buffer.pop();
      }
      _buffer.add( item );
    }

    @Override
    public void onComplete()
    {
      T value;
      while ( null != ( value = _buffer.pop() ) )
      {
        super.onNext( value );
      }
      super.onComplete();
    }

    @Override
    public void onError( @Nonnull final Throwable throwable )
    {
      _buffer.clear();
      super.onError( throwable );
    }
  }
}
