package streak;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface FilteringOperators2<T>
  extends StreamExtension<T>
{
  @Nonnull
  default Flow.Stream<T> filter( @Nonnull final Predicate<T> predicate )
  {
    return compose( p -> new PredicateFilterPublisher<>( p, predicate ) );
  }

  @Nonnull
  default Flow.Stream<T> take( final int count )
  {
    return compose( p -> new TakeFilterPublisher<>( p, count ) );
  }

  @Nonnull
  default Flow.Stream<T> first()
  {
    return take( 1 );
  }

  @Nonnull
  default Flow.Stream<T> dropWhile( @Nonnull final Predicate<T> predicate )
  {
    return compose( p -> new DropWhileOperator<>( p, predicate ) );
  }

  @Nonnull
  default Flow.Stream<T> skipUntil( @Nonnull final Predicate<T> predicate )
  {
    return dropWhile( predicate.negate() );
  }

  /**
   * drops consecutive equal elements
   */
  @Nonnull
  default Flow.Stream<T> dropConsecutiveDuplicates()
  {
    return compose( DropConsecutiveDuplicatesOperator::new );
  }

  @Nonnull
  default Flow.Stream<T> skipConsecutiveDuplicates()
  {
    return dropConsecutiveDuplicates();
  }
}
