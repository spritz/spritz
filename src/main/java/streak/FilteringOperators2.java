package streak;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface FilteringOperators2<T>
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
  default Flow.Stream<T> dropConsecutiveDuplicates()
  {
    return compose( DropConsecutiveDuplicatesOperator::new );
  }

  default Flow.Stream<T> skipConsecutiveDuplicates()
  {
    return dropConsecutiveDuplicates();
  }
}
