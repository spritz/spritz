package streak.internal.sources;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class FailStreamSource<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Throwable _error;

  FailStreamSource( @Nonnull final Throwable error )
  {
    _error = Objects.requireNonNull( error );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>();
    subscriber.onSubscribe( subscription );
    subscriber.onError( _error );
    subscription.dispose();
  }

  private static final class WorkerSubscription<T>
    implements Flow.Subscription
  {
    private boolean _done;

    private WorkerSubscription()
    {
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
