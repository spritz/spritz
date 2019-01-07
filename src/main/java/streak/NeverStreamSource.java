package streak;

import javax.annotation.Nonnull;
import streak.Subscriber;
import streak.Subscription;
import streak.AbstractStream;

final class NeverStreamSource<T>
  extends AbstractStream<T>
{
  NeverStreamSource()
  {
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( new WorkerSubscription<T>() );
  }

  private static final class WorkerSubscription<T>
    implements Subscription
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
