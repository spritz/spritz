package streak.internal.sources;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class NeverStreamSource<T>
  extends AbstractStream<T>
{
  NeverStreamSource()
  {
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( new WorkerSubscription<T>() );
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
