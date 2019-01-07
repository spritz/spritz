package streak.internal.sources;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Subscriber;
import streak.Subscription;
import streak.internal.AbstractStream;

final class StaticStreamSource<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final T[] _data;

  StaticStreamSource( @Nonnull final T[] data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _data );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Subscription
  {
    private final Subscriber<? super T> _subscriber;
    private final T[] _data;
    /**
     * Index into data.
     * _offset == _data.length implies next action is onComplete.
     * _offset == _data.length + 1 implies cancelled or onComplete has been invoked.
     */
    private int _offset;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, @Nonnull final T[] data )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _data = data;
      _offset = 0;
    }

    void pushData()
    {
      while ( _offset < _data.length && isNotDisposed() )
      {
        final T item = _data[ _offset ];
        _offset++;
        _subscriber.onNext( item );
      }
      if ( isNotDisposed() )
      {
        _subscriber.onComplete();
        dispose();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
      _offset = _data.length + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisposed()
    {
      return _offset > _data.length;
    }
  }
}
