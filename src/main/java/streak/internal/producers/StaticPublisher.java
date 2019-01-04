package streak.internal.producers;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class StaticPublisher<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final T[] _data;

  StaticPublisher( @Nonnull final T[] data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _data );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Flow.Subscription
  {
    private final Flow.Subscriber<? super T> _subscriber;
    private final T[] _data;
    /**
     * Index into data.
     * _offset == _data.length implies next action is onComplete.
     * _offset == _data.length + 1 implies cancelled or onComplete has been invoked.
     */
    private int _offset;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, @Nonnull final T[] data )
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
