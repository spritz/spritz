package streak.internal.sources;

import java.util.Objects;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import streak.Subscriber;
import streak.Subscription;
import streak.internal.AbstractStream;

final class GenerateStreamSource<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Callable<T> _callable;

  GenerateStreamSource( @Nonnull final Callable<T> callable )
  {
    _callable = Objects.requireNonNull( callable );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _callable );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Subscription
  {
    private final Subscriber<? super T> _subscriber;
    @Nonnull
    private final Callable<T> _callable;
    private boolean _done;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, @Nonnull final Callable<T> callable )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _callable = callable;
    }

    void pushData()
    {
      try
      {
        while ( isNotDisposed() )
        {
          _subscriber.onNext( _callable.call() );
        }
      }
      catch ( final Throwable error )
      {
        _subscriber.onError( error );
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
