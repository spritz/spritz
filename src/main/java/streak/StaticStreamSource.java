package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

final class StaticStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final T[] _data;

  StaticStreamSource( @Nonnull final T[] data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
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
      while ( _offset < _data.length && isActive() )
      {
        final T item = _data[ _offset ];
        _offset++;
        _subscriber.onNext( item );
      }
      if ( isActive() )
      {
        _subscriber.onComplete();
        cancel();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
      _offset = _data.length + 1;
    }

    private boolean isActive()
    {
      return _offset <= _data.length;
    }
  }
}
