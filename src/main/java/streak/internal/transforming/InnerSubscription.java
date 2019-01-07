package streak.internal.transforming;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Stream;
import streak.Subscriber;
import streak.Subscription;

final class InnerSubscription<T>
  implements Subscription, Subscriber<T>
{
  interface ContainerSubscription<T>
  {
    void onError( @Nonnull Throwable throwable );

    void completeInner( @Nonnull InnerSubscription<T> innerSubscription );
  }

  @Nonnull
  private final ContainerSubscription<T> _container;
  @Nonnull
  private final Stream<T> _upstream;
  @Nonnull
  private final Subscriber<? super T> _downstream;
  private Subscription _upstreamSubscription;

  InnerSubscription( @Nonnull final ContainerSubscription<T> container,
                     @Nonnull final Subscriber<? super T> downstream,
                     @Nonnull final Stream<T> upstream )
  {
    _container = Objects.requireNonNull( container );
    _downstream = Objects.requireNonNull( downstream );
    _upstream = Objects.requireNonNull( upstream );
  }

  void pushData()
  {
    _upstream.subscribe( this );
  }

  @Override
  public void dispose()
  {
    if ( null != _upstreamSubscription )
    {
      _upstreamSubscription.dispose();
      _upstreamSubscription = null;
    }
  }

  @Override
  public boolean isDisposed()
  {
    return null == _upstreamSubscription;
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
    _downstream.onNext( item );
  }

  @Override
  public void onError( @Nonnull final Throwable throwable )
  {
    _upstreamSubscription = null;
    _container.onError( throwable );
  }

  @Override
  public void onComplete()
  {
    _upstreamSubscription = null;
    _container.completeInner( this );
  }
}
