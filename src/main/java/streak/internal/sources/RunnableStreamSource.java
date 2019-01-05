package streak.internal.sources;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class RunnableStreamSource<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Runnable _runnable;

  RunnableStreamSource( @Nonnull final Runnable runnable )
  {
    _runnable = Objects.requireNonNull( runnable );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _runnable );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Flow.Subscription
  {
    private final Flow.Subscriber<? super T> _subscriber;
    @Nonnull
    private final Runnable _runnable;
    private boolean _done;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, @Nonnull final Runnable runnable )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _runnable = runnable;
    }

    void pushData()
    {
      try
      {
        _runnable.run();
      }
      catch ( final Throwable error )
      {
        _subscriber.onError( error );
        return;
      }
      _subscriber.onComplete();
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
