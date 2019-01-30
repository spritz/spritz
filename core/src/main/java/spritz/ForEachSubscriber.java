package spritz;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

final class ForEachSubscriber<T>
  implements Subscriber<T>
{
  @Nonnull
  private final Consumer<T> _action;

  ForEachSubscriber( @Nonnull final Consumer<T> action )
  {
    _action = Objects.requireNonNull( action );
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    _action.accept( item );
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
  }

  @Override
  public void onComplete()
  {
  }
}
