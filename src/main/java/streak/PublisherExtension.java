package streak;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface PublisherExtension<T>
  extends StreamExtension<T>
{
  default Flow.Stream<T> filter( @Nonnull final Predicate<T> predicate )
  {
    return compose( p -> new PredicateFilterPublisher<>( p, predicate ) );
  }

  default Flow.Stream<T> take( final int count )
  {
    return compose( p -> new TakeFilterPublisher<>( p, count ) );
  }

  default Flow.Stream<T> first()
  {
    return take( 1 );
  }

  default Flow.Stream<T> dropWhile( @Nonnull final Predicate<T> predicate )
  {
    return compose( p -> new DropWhileOperator<>( p, predicate ) );
  }

  default Flow.Stream<T> skipUntil( @Nonnull final Predicate<T> predicate )
  {
    return dropWhile( predicate.negate() );
  }

  /**
   * drops consecutive equal elements
   */
  default Flow.Stream<T> skipConsecutiveDuplicates()
  {
    return compose( SkipConsecutiveDuplicatesOperator::new );
  }

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
