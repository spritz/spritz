package streak.internal.consuming;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import streak.Subscriber;
import streak.internal.StreamExtension;
import streak.internal.ValidatingSubscriber;

public interface ConsumingOperators<T>
  extends StreamExtension<T>
{
  default void forEach( @Nonnull final Consumer<T> action )
  {
    terminate( () -> new ForEachSubscriber<>( action ) );
  }

  default void terminate( @Nonnull final Supplier<Subscriber<T>> terminateFunction )
  {
    self().subscribe( new ValidatingSubscriber<>( terminateFunction.get() ) );
  }
}
