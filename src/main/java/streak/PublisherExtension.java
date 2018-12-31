package streak;

import streak.internal.consuming.ConsumingOperators;
import streak.internal.filtering.FilteringOperators;
import streak.internal.transforming.TransformingOperators;

public interface PublisherExtension<T>
  extends FilteringOperators<T>, TransformingOperators<T>, ConsumingOperators<T>
{
}
