package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

final class InnerSubscription<T>
  extends AbstractSubscription<T, Stream<T>>
  implements Subscriber<T>
{
  interface ContainerSubscription<T>
  {
    void onError( @Nonnull Throwable throwable );

    void completeInner( @Nonnull InnerSubscription<T> innerSubscription );
  }

  @Nonnull
  private final ContainerSubscription<T> _container;
  private Subscription _upstreamSubscription;

  InnerSubscription( @Nonnull final Stream<T> stream,
                     @Nonnull final Subscriber<? super T> subscriber,
                     @Nonnull final ContainerSubscription<T> container )
  {
    super( stream, subscriber );
    _container = Objects.requireNonNull( container );
  }

  void pushData()
  {
    getStream().subscribe( this );
  }

  @Override
  final void doCancel()
  {
    if ( null != _upstreamSubscription )
    {
      _upstreamSubscription.cancel();
      _upstreamSubscription = null;
    }
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    assert null == _upstreamSubscription;
    _upstreamSubscription = subscription;
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    getSubscriber().onNext( item );
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    _upstreamSubscription = null;
    _container.onError( error );
  }

  @Override
  public void onComplete()
  {
    _upstreamSubscription = null;
    _container.completeInner( this );
  }
}
