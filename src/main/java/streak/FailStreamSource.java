package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

final class FailStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Throwable _error;

  FailStreamSource( @Nonnull final Throwable error )
  {
    _error = Objects.requireNonNull( error );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>();
    subscriber.onSubscribe( subscription );
    subscriber.onError( _error );
    subscription.cancel();
  }

  private static final class WorkerSubscription<T>
    implements Subscription
  {
    private WorkerSubscription()
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
