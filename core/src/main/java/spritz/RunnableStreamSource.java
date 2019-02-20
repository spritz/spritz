package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

final class RunnableStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Runnable _runnable;

  RunnableStreamSource( @Nonnull final Runnable runnable )
  {
    _runnable = Objects.requireNonNull( runnable );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _runnable );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    extends AbstractSubscription
  {
    private final Subscriber<? super T> _subscriber;
    @Nonnull
    private final Runnable _runnable;

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
        if ( !isDone() )
        {
          _subscriber.onError( error );
        }
        return;
      }
      if ( !isDone() )
      {
        _subscriber.onComplete();
      }
    }
  }
}
