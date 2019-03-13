package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class RunnableStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Runnable _runnable;

  RunnableStreamSource( @Nullable final String name, @Nonnull final Runnable runnable )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "fromRunnable" ) : null );
    _runnable = Objects.requireNonNull( runnable );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
    return subscription;
  }

  private static final class WorkerSubscription<T>
    extends AbstractStreamSubscription<T, RunnableStreamSource<T>>
  {
    WorkerSubscription( @Nonnull final RunnableStreamSource<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    void pushData()
    {
      try
      {
        getStream()._runnable.run();
      }
      catch ( final Throwable error )
      {
        if ( isNotDone() )
        {
          getSubscriber().onError( error );
        }
        return;
      }
      if ( isNotDone() )
      {
        getSubscriber().onComplete();
      }
    }
  }
}
