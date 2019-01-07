package streak;

import javax.annotation.Nonnull;
import streak.internal.consuming.ConsumingOperators;
import streak.internal.filtering.FilteringOperators;
import streak.internal.peeking.PeekingOperators;
import streak.internal.transforming.TransformingOperators;

public interface Stream<T>
  extends PeekingOperators<T>, FilteringOperators<T>, TransformingOperators<T>, ConsumingOperators<T>
{
  void subscribe( @Nonnull Subscriber<? super T> subscriber );
}
