package streak.internal.sources;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class GenerateStreamSource<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Callable<T> _supplier;

  GenerateStreamSource( @Nonnull final Callable<T> supplier )
  {
    _supplier = Objects.requireNonNull( supplier );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _supplier );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Flow.Subscription
  {
    private final Flow.Subscriber<? super T> _subscriber;
    @Nonnull
    private final Callable<T> _supplier;
    private boolean _done;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, @Nonnull final Callable<T> supplier )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _supplier = supplier;
    }

    void pushData()
    {
      try
      {
        while ( isNotDisposed() )
        {
          _subscriber.onNext( _supplier.call() );
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
