package streak.internal.transforming;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;

final class InnerSubscription<T>
  implements Flow.Subscription, Flow.Subscriber<T>
{
  interface ContainerSubscription<T>
  {
    void onError( @Nonnull Throwable throwable );

    void completeInner( @Nonnull InnerSubscription<T> innerSubscription );
  }

  @Nonnull
  private final ContainerSubscription<T> _container;
  @Nonnull
  private final Flow.Stream<T> _upstream;
  @Nonnull
  private final Flow.Subscriber<? super T> _downstream;
  private Flow.Subscription _upstreamSubscription;

  InnerSubscription( @Nonnull final ContainerSubscription<T> container,
                     @Nonnull final Flow.Subscriber<? super T> downstream,
                     @Nonnull final Flow.Stream<T> upstream )
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
  public void onSubscribe( @Nonnull final Flow.Subscription subscription )
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
