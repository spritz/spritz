package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class CreateStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final SourceCreator<T> _createFunction;

  CreateStreamSource( @Nullable final String name, @Nonnull final SourceCreator<T> createFunction )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "create" ) : null );
    _createFunction = Objects.requireNonNull( createFunction );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription();
    subscriber.onSubscribe( subscription );
    _createFunction.create( new SimpleSubscriberAdapter<>( subscriber, subscription ) );
  }

  private static final class WorkerSubscription
    implements Subscription
  {
    private boolean _cancelled;

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
      _cancelled = true;
    }

    boolean isCancelled()
    {
      return _cancelled;
    }
  }

  private static class SimpleSubscriberAdapter<T>
    implements SourceCreator.Observer<T>
  {
    private final Subscriber<? super T> _subscriber;
    private final WorkerSubscription _subscription;

    SimpleSubscriberAdapter( final Subscriber<? super T> subscriber, final WorkerSubscription subscription )
    {
      _subscriber = subscriber;
      _subscription = subscription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void next( @Nonnull final T item )
    {
      if ( !isCancelled() )
      {
        _subscriber.onNext( item );
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error( @Nonnull final Throwable throwable )
    {
      if ( !isCancelled() )
      {
        _subscriber.onError( throwable );
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void complete()
    {
      if ( !isCancelled() )
      {
        _subscriber.onComplete();
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCancelled()
    {
      return _subscription.isCancelled();
    }
  }
}
