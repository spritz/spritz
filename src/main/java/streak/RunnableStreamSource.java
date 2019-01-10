package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

final class RunnableStreamSource<T>
  implements Stream<T>
{
  @Nonnull
  private final Runnable _runnable;

  RunnableStreamSource( @Nonnull final Runnable runnable )
  {
    _runnable = Objects.requireNonNull( runnable );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _runnable );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Subscription
  {
    private final Subscriber<? super T> _subscriber;
    @Nonnull
    private final Runnable _runnable;
    private boolean _done;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, @Nonnull final Runnable runnable )
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
        if ( !_done )
        {
          _subscriber.onError( error );
        }
        return;
      }
      if ( !_done )
      {
        _subscriber.onComplete();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
      _done = true;
    }
  }
}
