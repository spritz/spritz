package org.realityforge.rxs;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

final class ForEachSubscriber<T>
  implements Flow.Subscriber<T>
{
  @Nonnull
  private final Consumer<T> _action;
  private Flow.Subscription _subscription;

  ForEachSubscriber( @Nonnull final Consumer<T> action )
  {
    _action = Objects.requireNonNull( action );
  }

  @Override
  public void onSubscribe( @Nonnull final Flow.Subscription subscription )
  {
    _subscription = subscription;
    _subscription.request( 1 );
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    _action.accept( item );
    _subscription.request( 1 );
  }

  @Override
  public void onError( @Nonnull final Throwable throwable )
  {
  }

  @Override
  public void onComplete()
  {
  }
}
