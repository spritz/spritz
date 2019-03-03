package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class LastOperator<T>
  extends AbstractStream<T, T>
{
  private final int _maxBufferSize;

  LastOperator( @Nullable final String name, @Nonnull final Stream<T> upstream, final int maxBufferSize )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "last", String.valueOf( maxBufferSize ) ) : null, upstream );
    _maxBufferSize = maxBufferSize;
    assert maxBufferSize > 0;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends PassThroughSubscription<T, LastOperator<T>>
  {
    @Nonnull
    private final CircularBuffer<T> _buffer;

    WorkerSubscription( @Nonnull final LastOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
      _buffer = new CircularBuffer<>( stream._maxBufferSize );
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
    public void onError( @Nonnull final Throwable error )
    {
      _buffer.clear();
      super.onError( error );
    }
  }
}
