package streak;

import arez.Disposable;
import javax.annotation.Nonnull;

public final class Flow
{
  private Flow()
  {
  }

  public interface Subscription
    extends Disposable
  {
  }

  public interface Subscriber<T>
  {
    void onSubscribe( @Nonnull Subscription subscription );

    void onNext( @Nonnull T item );

    void onError( @Nonnull Throwable throwable );

    void onComplete();
  }

  public interface Stream<T>
    extends PublisherExtension<T>
  {
    void subscribe( @Nonnull Subscriber<? super T> subscriber );
  }
}
