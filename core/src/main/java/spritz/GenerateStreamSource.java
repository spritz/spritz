package spritz;

import java.util.Objects;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;

final class GenerateStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Callable<T> _callable;

  GenerateStreamSource( @Nonnull final Callable<T> callable )
  {
    _callable = Objects.requireNonNull( callable );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _callable );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    extends AbstractSubscription
  {
    private final Subscriber<? super T> _subscriber;
    @Nonnull
    private final Callable<T> _callable;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, @Nonnull final Callable<T> callable )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _callable = callable;
    }

    void pushData()
    {
      try
      {
        while ( !isDone() )
        {
          _subscriber.onNext( _callable.call() );
        }
      }
      catch ( final Throwable error )
      {
        _subscriber.onError( error );
      }
    }
  }
}
