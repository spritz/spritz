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
    final PassThroughSubscription<T, CreateStreamSource<T>> subscription =
      new PassThroughSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    _createFunction.create( new SimpleSubscriberAdapter<>( subscriber, subscription ) );
  }

  private static class SimpleSubscriberAdapter<T>
    implements SourceCreator.Observer<T>
  {
    @Nonnull
    private final Subscriber<? super T> _subscriber;
    @Nonnull
    private final PassThroughSubscription<T, CreateStreamSource<T>> _subscription;

    SimpleSubscriberAdapter( @Nonnull final Subscriber<? super T> subscriber,
                             @Nonnull final PassThroughSubscription<T, CreateStreamSource<T>> subscription )
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
