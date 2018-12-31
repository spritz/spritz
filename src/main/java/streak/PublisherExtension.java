package streak;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import streak.internal.filtering.FilteringOperators;
import streak.internal.transforming.TransformingOperators;

public interface PublisherExtension<T>
  extends FilteringOperators<T>, TransformingOperators<T>
{
  default void forEach( @Nonnull final Consumer<T> action )
  {
    terminate( () -> new ForEachSubscriber<>( action ) );
  }

  default void terminate( @Nonnull final Supplier<Flow.Subscriber<T>> terminateFunction )
  {
    self().subscribe( new ValidatingSubscriber<>( terminateFunction.get() ) );
  }
}
