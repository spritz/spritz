package streak.internal.filtering;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import streak.Stream;
import streak.internal.StreamExtension;

/**
 * Operators for filtering elements from a stream.
 *
 * @param <T> The type of the elements that the stream consumes and emits.
 */
public interface FilteringOperators<T>
  extends StreamExtension<T>
{
  /**
   * Filter the elements emitted by this stream using the specified {@link Predicate}.
   * Any elements that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other elements will be dropped.
   *
   * @param predicate The predicate to apply to each element.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> filter( @Nonnull final Predicate<? super T> predicate )
  {
    return compose( p -> new PredicateFilterStream<>( p, predicate ) );
  }

  /**
   * Drop all elements from this stream, only emitting completion or failed signal.
   *
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> ignoreElements()
  {
    return filter( e -> false );
  }

  /**
   * Filter the elements if they have been previously emitted.
   * To determine whether an element has been previous emitted the {@link Object#equals(Object)}
   * and {@link Object#hashCode()} must be correctly implemented for elements type.
   *
   * <p>WARNING: It should be noted that every distinct element is retained until the stream
   * completes. As a result this operator can cause significant amount of memory pressure if many
   * distinct elements exist or the stream persists for a long time.</p>
   *
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> distinct()
  {
    return compose( DistinctOperator::new );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} elements in length.
   * If {@code maxSize} is reached then the element will be passed downstream, the downstream will be
   * completed and then the upstream will be disposed. This method is an alias for {@link #limit(int)}
   *
   * @param maxSize The maximum number of elements returned by the stream.
   * @return the new stream.
   * @see #limit(int)
   */
  @Nonnull
  default Stream<T> take( final int maxSize )
  {
    return limit( maxSize );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} elements in length.
   * If {@code maxSize} is reached then the element will be passed downstream, the downstream will be
   * completed and then the upstream will be disposed. This method is an alias for {@link #take(int)}
   *
   * @param maxSize The maximum number of elements returned by the stream.
   * @return the new stream.
   * @see #take(int)
   */
  @Nonnull
  default Stream<T> limit( final int maxSize )
  {
    return compose( p -> new LimitOperator<>( p, maxSize ) );
  }

  /**
   * Pass the first element downstream, complete the downstream and dispose the upstream.
   * This method is an alias for {@link #take(int)} or {@link #limit(int)} where <code>1</code> is
   * passed as the parameter.
   *
   * @return the new stream.
   * @see #take(int)
   * @see #limit(int)
   */
  @Nonnull
  default Stream<T> first()
  {
    return take( 1 );
  }

  /**
   * Drop the first {@code count} elements of this stream. If the stream contains fewer
   * than {@code count} elements then the stream will effectively be an empty stream.
   *
   * @param count the number of elements to drop.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> skip( final int count )
  {
    return compose( p -> new SkipOperator<>( p, count ) );
  }

  /**
   * Drop all elements except for the last element.
   * Once the complete signal has been received the operator will emit the last element received
   * if any prior to sending the complete signal. This is equivalent to invoking the {@link #last(int)}
   * method and passing the value <code>1</code> to the parameter <code>maxElements</code>.
   *
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  default Stream<T> last()
  {
    return last( 1 );
  }

  /**
   * Drop all elements except for the last {@code maxElements} elements.
   * This operator will buffer up to {@code maxElements} elements until it receives the complete
   * signal and then it will send all the buffered elements and the complete signal. If less than
   * {@code maxElements} are emitted by the upstream then it is possible for the downstream to receive
   * less than {@code maxElements} elements.
   *
   * @param maxElements the maximum number
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> last( final int maxElements )
  {
    return compose( p -> new LastOperator<>( p, maxElements ) );
  }

  /**
   * Drop all elements except for the last {@code maxElements} elements.
   * This operator will buffer up to {@code maxElements} elements until it receives the complete
   * signal and then it will send all the buffered elements and the complete signal. If less than
   * {@code maxElements} are emitted by the upstream then it is possible for the downstream to receive
   * less than {@code maxElements} elements. This method is an alias for the {@link #last(int)} method.
   *
   * @param maxElements the maximum number
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  default Stream<T> takeLast( final int maxElements )
  {
    return last( maxElements );
  }

  /**
   * Drop elements from this stream until an element no longer matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, no elements will be emitted from this stream. Once
   * the first element is encountered for which the {@code predicate} returns false, all subsequent
   * elements will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
   * to {@link #dropUntil(Predicate)} if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the new stream.
   * @see #dropUntil(Predicate)
   */
  @Nonnull
  default Stream<T> dropWhile( @Nonnull final Predicate<? super T> predicate )
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
   * @return the new stream.
   * @see #dropWhile(Predicate)
   */
  @Nonnull
  default Stream<T> dropUntil( @Nonnull final Predicate<? super T> predicate )
  {
    return dropWhile( predicate.negate() );
  }

  /**
   * Return elements from this stream until an element fails to match the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, elements will be emitted from this stream. Once
   * the first element is encountered for which the {@code predicate} returns false, the stream will
   * be completed and the upstream disposed. This is equivalent to {@link #takeUntil(Predicate)}
   * if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the new stream.
   * @see #takeUntil(Predicate)
   */
  @Nonnull
  default Stream<T> takeWhile( @Nonnull final Predicate<? super T> predicate )
  {
    return compose( p -> new TakeWhileOperator<>( p, predicate ) );
  }

  /**
   * Return elements from this stream until an element matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, elements will be emitted from this stream. Once
   * the first element is encountered for which the {@code predicate} returns true, the stream will
   * be completed and the upstream disposed. This is equivalent to {@link #takeWhile(Predicate)}
   * if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the new stream.
   * @see #takeUntil(Predicate)
   */
  @Nonnull
  default Stream<T> takeUntil( @Nonnull final Predicate<? super T> predicate )
  {
    return takeWhile( predicate.negate() );
  }

  /**
   * Drops elements from the stream if they are equal to the previous element emitted in the stream.
   * The elements are tested for equality using the {@link Objects#equals(Object, Object)} method.
   * This method is an alias for {@link #skipConsecutiveDuplicates()}. It is equivalent to invoking
   * {@link #filterSuccessive(SuccessivePredicate)} passing a {@link SuccessivePredicate} filters
   * out successive elements that are equal.
   *
   * @return the new stream.
   * @see #skipConsecutiveDuplicates()
   */
  @Nonnull
  default Stream<T> dropConsecutiveDuplicates()
  {
    return filterSuccessive( ( prev, current ) -> !Objects.equals( prev, current ) );
  }

  /**
   * Drops elements from the stream if they are equal to the previous element emitted in the stream.
   * The elements are tested for equality using the {@link Objects#equals(Object, Object)} method.
   * This method is an alias for {@link #dropConsecutiveDuplicates()}.
   *
   * @return the new stream.
   * @see #dropConsecutiveDuplicates()
   */
  @Nonnull
  default Stream<T> skipConsecutiveDuplicates()
  {
    return dropConsecutiveDuplicates();
  }

  /**
   * Filter consecutive elements emitted by this stream using the specified {@link SuccessivePredicate}.
   * Any candidate elements that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other elements will be dropped. The predicate passes the last emitted element
   * as well as the candidate element.
   *
   * @param predicate the comparator to determine whether two successive elements are equal.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> filterSuccessive( @Nonnull final SuccessivePredicate<T> predicate )
  {
    return compose( s -> new FilterSuccessiveOperator<>( s, predicate ) );
  }

  /**
   * Emits the next item emitted by a stream, then periodically emits the latest item (if any)
   * when the specified timeout elapses between them.
   *
   * @param timeout the minimum time between success items being emitted.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> throttleLatest( final int timeout )
  {
    return compose( s -> new ThrottleLatestOperator<>( s, timeout ) );
  }
}
