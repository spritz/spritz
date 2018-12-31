package streak.internal.consuming;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import streak.Flow;

final class ForEachSubscriber<T>
  implements Flow.Subscriber<T>
{
  @Nonnull
  private final Consumer<T> _action;

  ForEachSubscriber( @Nonnull final Consumer<T> action )
  {
    _action = Objects.requireNonNull( action );
  }

  @Override
  public void onSubscribe( @Nonnull final Flow.Subscription subscription )
  {
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    _action.accept( item );
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
