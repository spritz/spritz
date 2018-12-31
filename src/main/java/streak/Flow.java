package streak;

import arez.Disposable;
import javax.annotation.Nonnull;
import streak.internal.consuming.ConsumingOperators;
import streak.internal.filtering.FilteringOperators;
import streak.internal.peeking.PeekingOperators;
import streak.internal.transforming.TransformingOperators;

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
    extends PeekingOperators<T>, FilteringOperators<T>, TransformingOperators<T>, ConsumingOperators<T>
  {
    void subscribe( @Nonnull Subscriber<? super T> subscriber );
  }
}
