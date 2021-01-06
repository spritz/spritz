package spritz;

import javax.annotation.Nonnull;

public interface Subscriber<T>
{
  void onSubscribe( @Nonnull Subscription subscription );

  void onItem( @Nonnull T item );

  void onError( @Nonnull Throwable error );

  void onComplete();
}
