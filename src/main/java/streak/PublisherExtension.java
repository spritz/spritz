package streak;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface PublisherExtension<T>
{
  default Flow.Publisher<T> filter( @Nonnull final Predicate<T> predicate )
  {
    return compose( p -> new PredicateFilterPublisher<>( p, predicate ) );
  }

  default Flow.Publisher<T> take( final int count )
  {
    return compose( p -> new TakeFilterPublisher<>( p, count ) );
  }

  default Flow.Publisher<T> first()
  {
    return take( 1 );
  }

  default Flow.Publisher<T> dropWhile( @Nonnull final Predicate<T> predicate )
  {
    return compose( p -> new DropWhileOperator<>( p, predicate ) );
  }

  default Flow.Publisher<T> skipUntil( @Nonnull final Predicate<T> predicate )
  {
    return dropWhile( predicate.negate() );
  }

  /**
   * drops consecutive equal elements
   */
  default Flow.Publisher<T> skipConsecutiveDuplicates()
  {
    return compose( SkipConsecutiveDuplicatesOperator::new );
  }

  default <DownstreamT> Flow.Publisher<DownstreamT> map( @Nonnull final Function<T, DownstreamT> transform )
  {
    return compose( p -> new MapPublisher<>( p, transform ) );
  }

  default void forEach( @Nonnull final Consumer<T> action )
  {
    terminate( () -> new ForEachSubscriber<>( action ) );
  }

  @Nonnull
  default <DownstreamT> Flow.Publisher<DownstreamT> compose( @Nonnull final Function<Flow.Publisher<T>, Flow.Publisher<DownstreamT>> composeFunction )
  {
    return composeFunction.apply( new ValidatingPublisher<>( self() ) );
  }

  default void terminate( @Nonnull final Supplier<Flow.Subscriber<T>> terminateFunction )
  {
    self().subscribe( new ValidatingSubscriber<>( terminateFunction.get() ) );
  }

  @Nonnull
  Flow.Publisher<T> self();
}
