package spritz;

import java.util.Objects;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class GenerateStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Callable<T> _callable;

  GenerateStreamSource( @Nullable final String name, @Nonnull final Callable<T> callable )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "fromCallable" ) : null );
    _callable = Objects.requireNonNull( callable );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    extends AbstractSubscription<T, GenerateStreamSource<T>>
  {
    WorkerSubscription( @Nonnull final GenerateStreamSource<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    void pushData()
    {
      final Subscriber<? super T> subscriber = getSubscriber();
      try
      {
        while ( !isDone() )
        {
          subscriber.onNext( getStream()._callable.call() );
        }
      }
      catch ( final Throwable error )
      {
        subscriber.onError( error );
      }
    }
  }
}
