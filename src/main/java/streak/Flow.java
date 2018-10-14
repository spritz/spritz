package streak;

import javax.annotation.Nonnull;

public final class Flow
{
  private Flow()
  {
  }

  public interface Subscription
    extends Disposable
  {
    /**
     * @param count number of items to request. Must be positive (this is different from reactive-streams API)
     */
    void request( int count );
  }

  public interface Subscriber<T>
  {
    void onSubscribe( @Nonnull Subscription subscription );

    void onNext( @Nonnull T item );

    void onError( @Nonnull Throwable throwable );

    void onComplete();
  }

  public interface Publisher<T>
    extends PublisherExtension<T>
  {
    void subscribe( @Nonnull Subscriber<? super T> subscriber );
  }
}
