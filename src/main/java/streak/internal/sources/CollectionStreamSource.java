package streak.internal.sources;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class CollectionStreamSource<T>
  extends AbstractStream<T>
{
  private final Collection<T> _data;

  CollectionStreamSource( @Nonnull final Collection<T> data )
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
    private final Collection<T> _data;
    private boolean _done;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, @Nonnull final Collection<T> data )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _data = Objects.requireNonNull( data );
    }

    void pushData()
    {
      for ( final T item : _data )
      {
        if ( isDisposed() )
        {
          break;
        }
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
      _done = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisposed()
    {
      return _done;
    }
  }
}
