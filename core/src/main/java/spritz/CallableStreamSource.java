package spritz;

import java.util.Objects;
import java.util.concurrent.Callable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class CallableStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Callable<T> _callable;

  CallableStreamSource( @Nullable final String name, @Nonnull final Callable<T> callable )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "fromCallable" ) : null );
    _callable = Objects.requireNonNull( callable );
  }

  @Override
  @Nonnull
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
    return subscription;
  }

  private static final class WorkerSubscription<T>
    extends AbstractStreamSubscription<T, CallableStreamSource<T>>
  {
    WorkerSubscription( @Nonnull final CallableStreamSource<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    void pushData()
    {
      final Subscriber<? super T> subscriber = getSubscriber();
      try
      {
        while ( isNotDone() )
        {
          subscriber.onItem( getStream()._callable.call() );
        }
      }
      catch ( final Throwable error )
      {
        subscriber.onError( error );
      }
    }
  }
}
