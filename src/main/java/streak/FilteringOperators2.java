package streak;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Operators for filtering elements from a stream.
 *
 * @param <T> The type of the elements that the stream consumes and emits.
 */
public interface FilteringOperators2<T>
  extends StreamExtension<T>
{
  /**
   * Filter the elements emitted by this stream using the specified {@link Predicate}.
   * Any elements that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other elements will be dropped.
   *
   * @param predicate The predicate to apply to each element.
   * @return the stream.
   */
  @Nonnull
  default Flow.Stream<T> filter( @Nonnull final Predicate<? super T> predicate )
  {
    return compose( p -> new PredicateFilterPublisher<>( p, predicate ) );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} elements in length.
   * If {@code maxSize} is reached then the element will be passed downstream, the downstream will be
   * completed and then the upstream will be disposed. This method is an alias for {@link #limit(int)}
   *
   * @param maxSize The maximum number of elements returned by the stream.
   * @return the stream.
   * @see #limit(int)
   */
  @Nonnull
  default Flow.Stream<T> take( final int maxSize )
  {
    return limit( maxSize );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} elements in length.
   * If {@code maxSize} is reached then the element will be passed downstream, the downstream will be
   * completed and then the upstream will be disposed. This method is an alias for {@link #take(int)}
   *
   * @param maxSize The maximum number of elements returned by the stream.
   * @return the stream.
   * @see #take(int)
   */
  @Nonnull
  default Flow.Stream<T> limit( final int maxSize )
  {
    return compose( p -> new LimitOperator<>( p, maxSize ) );
  }

  /**
   * Pass the first element downstream, complete the downstream and dispose the upstream.
   * This method is an alias for {@link #take(int)} or {@link #limit(int)} where <code>1</code> is
   * passed as the parameter.
   *
   * @return the stream.
   * @see #take(int)
   * @see #limit(int)
   */
  @Nonnull
  default Flow.Stream<T> first()
  {
    return take( 1 );
  }

  /**
   * Drop the first {@code count} elements of this stream. If the stream contains fewer
   * than {@code count} elements then the stream will effectively be an empty stream.
   *
   * @param count the number of elements to drop.
   * @return the stream.
   */
  @Nonnull
  default Flow.Stream<T> skip( final int count )
  {
    return compose( p -> new SkipOperator<>( p, count ) );
  }

  /**
   * Drop elements from this stream until an element no longer match the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, no elements will be emitted from this stream. Once
   * the first element is encountered for which the {@code predicate} returns false, all subsequent
   * elements will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
   * to {@link #dropUntil(Predicate)} if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the stream.
   * @see #dropUntil(Predicate)
   */
  @Nonnull
  default Flow.Stream<T> dropWhile( @Nonnull final Predicate<? super T> predicate )
  {
    return compose( p -> new DropWhileOperator<>( p, predicate ) );
  }

  /**
   * Drop elements from this stream until an element matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, no elements will be emitted from this stream. Once
   * the first element is encountered for which the {@code predicate} returns true, all subsequent
   * elements will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
   * to {@link #dropWhile(Predicate)} if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the stream.
   * @see #dropWhile(Predicate)
   */
  @Nonnull
  default Flow.Stream<T> dropUntil( @Nonnull final Predicate<? super T> predicate )
  {
    return dropWhile( predicate.negate() );
  }

  /**
   * Drops elements from the stream if they are equal to the previous element emitted in the stream.
   * The elements are tested for equality using the {@link Objects#equals(Object, Object)} method.
   * This method is an alias for {@link #skipConsecutiveDuplicates()}.
   *
   * @return the stream.
   * @see #skipConsecutiveDuplicates()
   */
  @Nonnull
  default Flow.Stream<T> dropConsecutiveDuplicates()
  {
    return compose( DropConsecutiveDuplicatesOperator::new );
  }

  /**
   * Drops elements from the stream if they are equal to the previous element emitted in the stream.
   * The elements are tested for equality using the {@link Objects#equals(Object, Object)} method.
   * This method is an alias for {@link #dropConsecutiveDuplicates()}.
   *
   * @return the stream.
   * @see #dropConsecutiveDuplicates()
   */
  @Nonnull
  default Flow.Stream<T> skipConsecutiveDuplicates()
  {
    return dropConsecutiveDuplicates();
  }
}
