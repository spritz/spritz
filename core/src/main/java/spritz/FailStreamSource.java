package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class FailStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Throwable _error;

  FailStreamSource( @Nullable final String name, @Nonnull final Throwable error )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "fail" ) : null );
    _error = Objects.requireNonNull( error );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription();
    subscriber.onSubscribe( subscription );
    subscriber.onError( _error );
    subscription.cancel();
  }

  private static final class WorkerSubscription
    implements Subscription
  {
    WorkerSubscription()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
    }
  }
}
