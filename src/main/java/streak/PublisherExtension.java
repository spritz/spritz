package streak;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface PublisherExtension<T>
  extends StreamExtension<T>, FilteringOperators2<T>
{
  default <DownstreamT> Flow.Stream<DownstreamT> map( @Nonnull final Function<T, DownstreamT> transform )
  {
    return compose( p -> new MapPublisher<>( p, transform ) );
  }

  default void forEach( @Nonnull final Consumer<T> action )
  {
    terminate( () -> new ForEachSubscriber<>( action ) );
  }

  default void terminate( @Nonnull final Supplier<Flow.Subscriber<T>> terminateFunction )
  {
    self().subscribe( new ValidatingSubscriber<>( terminateFunction.get() ) );
  }
}
