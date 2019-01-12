package streak;

import java.util.ArrayList;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public interface Stream<T>
{
  /**
   * The maximum concurrency of {@link #mergeMap(Function)} operator that does not specify concurrency.
   * This value is high enough that it is expected to be effectively infinite while not causing numeric
   * overflow in either JS or java compile targets.
   */
  int DEFAULT_MAX_CONCURRENCY = 1024 * 1024;

  void subscribe( @Nonnull Subscriber<? super T> subscriber );

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling subscription.
   *
   * @param action the function called before signalling subscription.
   * @return the new stream.
   * @see #afterSubscribe(Consumer)
   */
  @Nonnull
  default Stream<T> onSubscribe( @Nonnull final Consumer<Subscription> action )
  {
    return new PeekOperator<>( this, action, null, null, null, null, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling subscription.
   *
   * @param action the function called after signalling subscription.
   * @return the new stream.
   * @see #onSubscribe(Consumer)
   */
  @Nonnull
  default Stream<T> afterSubscribe( @Nonnull final Consumer<Subscription> action )
  {
    return new PeekOperator<>( this, null, action, null, null, null, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #onNext(Consumer)}.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #onNext(Consumer)
   */
  @Nonnull
  default Stream<T> tap( @Nonnull final Consumer<? super T> action )
  {
    return onNext( action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #onNext(Consumer)}.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #onNext(Consumer)
   */
  @Nonnull
  default Stream<T> peek( @Nonnull final Consumer<? super T> action )
  {
    return onNext( action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #peek(Consumer)}.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #peek(Consumer)
   * @see #tap(Consumer)
   * @see #afterNext(Consumer)
   */
  @Nonnull
  default Stream<T> onNext( @Nonnull final Consumer<? super T> action )
  {
    return new PeekOperator<>( this, null, null, action, null, null, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after each item is emitted. This method is an alias for {@link #peek(Consumer)}.
   *
   * @param action the function after each item is emitted.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> afterNext( @Nonnull final Consumer<? super T> action )
  {
    return new PeekOperator<>( this, null, null, null, action, null, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling error.
   *
   * @param action the function called before signalling error.
   * @return the new stream.
   * @see #afterError(Consumer)
   */
  @Nonnull
  default Stream<T> onError( @Nonnull final Consumer<Throwable> action )
  {
    return new PeekOperator<>( this, null, null, null, null, action, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling error.
   *
   * @param action the function called after signalling error.
   * @return the new stream.
   * @see #onError(Consumer)
   */
  @Nonnull
  default Stream<T> afterError( @Nonnull final Consumer<Throwable> action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, action, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete.
   *
   * @param action the function called when the stream completes.
   * @return the new stream.
   * @see #afterComplete(Runnable)
   */
  @Nonnull
  default Stream<T> onComplete( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, action, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete.
   *
   * @param action the function called when the stream completes.
   * @return the new stream.
   * @see #onComplete(Runnable)
   */
  @Nonnull
  default Stream<T> afterComplete( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, null, action, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before the stream is canceled by a downstream stage.
   *
   * @param action the function called before the stream is canceled by a downstream stage.
   * @return the new stream.
   * @see #afterCancel(Runnable)
   */
  @Nonnull
  default Stream<T> onCancel( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, null, null, action, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after the stream is canceled by a downstream stage.
   *
   * @param action the function called after the stream is canceled by a downstream stage.
   * @return the new stream.
   * @see #onCancel(Runnable)
   */
  @Nonnull
  default Stream<T> afterCancel( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, null, null, null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #onError(Consumer)} and
   * {@link #onComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param action the function called before signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #afterTerminate(Runnable)
   */
  @Nonnull
  default Stream<T> onTerminate( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, e -> action.run(), null, action, null, action, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #onError(Consumer)} and
   * {@link #onComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param action the function called after signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #onTerminate(Runnable)
   */
  @Nonnull
  default Stream<T> afterTerminate( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, e -> action.run(), null, action, null, action );
  }

  /**
   * Filter the items emitted by this stream using the specified {@link Predicate}.
   * Any items that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other items will be dropped.
   *
   * @param predicate the predicate to apply to each item.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> filter( @Nonnull final Predicate<? super T> predicate )
  {
    return compose( p -> new PredicateFilterStream<>( p, predicate ) );
  }

  /**
   * Remove items in the stream that are not instances of the specified {@code type} and return a stream of the specified type.
   *
   * @param <DownstreamT> the type of item emitted downstream.
   * @param type          the class of items to be emitted downstream.
   * @return the new stream.
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default <DownstreamT extends T> Stream<DownstreamT> ofType( @Nonnull final Class<DownstreamT> type )
  {
    return filter( type::isInstance ).map( i -> (DownstreamT) i );
  }

  /**
   * Drop all items from this stream, only emitting the completion or failed signal.
   *
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> ignoreElements()
  {
    return filter( e -> false );
  }

  /**
   * Filter the items if they have been previously emitted.
   * To determine whether an item has been previous emitted the {@link Object#equals(Object)}
   * and {@link Object#hashCode()} must be correctly implemented for items type.
   *
   * <p>WARNING: It should be noted that every distinct item is retained until the stream
   * completes. As a result this operator can cause significant amount of memory pressure if many
   * distinct items exist or the stream persists for a long time.</p>
   *
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> distinct()
  {
    return compose( DistinctOperator::new );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} items in length.
   * If {@code maxSize} is reached then the item will be passed downstream, the downstream will be
   * completed and then the upstream will be cancelled. This method is an alias for {@link #limit(int)}
   *
   * @param maxSize The maximum number of items returned by the stream.
   * @return the new stream.
   * @see #limit(int)
   */
  @Nonnull
  default Stream<T> take( final int maxSize )
  {
    return limit( maxSize );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} items in length.
   * If {@code maxSize} is reached then the item will be passed downstream, the downstream will be
   * completed and then the upstream will be cancelled. This method is an alias for {@link #take(int)}
   *
   * @param maxSize The maximum number of items returned by the stream.
   * @return the new stream.
   * @see #take(int)
   */
  @Nonnull
  default Stream<T> limit( final int maxSize )
  {
    return compose( p -> new LimitOperator<>( p, maxSize ) );
  }

  /**
   * Pass the first item downstream, complete the downstream and cancel the upstream.
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
   * Pass the first item downstream, complete the downstream and cancel the upstream.
   * If the stream is empty then signal an error of type {@link NoSuchElementException}.
   *
   * @return the new stream.
   * @see #first()
   */
  @Nonnull
  default Stream<T> firstOrError()
  {
    return first().errorIfEmpty( NoSuchElementException::new );
  }

  /**
   * Pass the first item downstream, complete the downstream and cancel the upstream.
   * If the stream is empty then emit the defaultValue specified as a parameter.
   *
   * @param defaultValue the default value emitted if the stream is empty.
   * @return the new stream.
   * @see #first()
   */
  @Nonnull
  default Stream<T> firstOrDefault( @Nonnull final T defaultValue )
  {
    return first().defaultIfEmpty( defaultValue );
  }

  /**
   * Emit an error if the stream completes and no items were emitted.
   * The error is created by invoking errorFactory when the error will be emitted.
   *
   * @param errorFactory the factory responsible for creating error.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> errorIfEmpty( @Nonnull final Supplier<Throwable> errorFactory )
  {
    return compose( p -> new ErrorIfEmptyOperator<>( p, errorFactory ) );
  }

  /**
   * Drop the first {@code count} items of this stream. If the stream contains fewer
   * than {@code count} items then the stream will effectively be an empty stream.
   *
   * @param count the number of items to drop.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> skip( final int count )
  {
    return compose( p -> new SkipOperator<>( p, count ) );
  }

  /**
   * Drop all items except for the last item.
   * Once the complete signal has been received the operator will emit the last item received
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
   * Drop all items except for the last item.
   * If the stream is empty then signal an error of type {@link NoSuchElementException}.
   *
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  default Stream<T> lastOrError()
  {
    return last().errorIfEmpty( NoSuchElementException::new );
  }

  /**
   * Drop all items except for the last item.
   * If the stream is empty then emit the defaultValue specified as a parameter.
   *
   * @param defaultValue the default value emitted if the stream is empty.
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  default Stream<T> lastOrDefault( @Nonnull final T defaultValue )
  {
    return last().defaultIfEmpty( defaultValue );
  }

  /**
   * Drop all items except for the last {@code maxElements} items.
   * This operator will buffer up to {@code maxElements} items until it receives the complete
   * signal and then it will send all the buffered items and the complete signal. If less than
   * {@code maxElements} are emitted by the upstream then it is possible for the downstream to receive
   * less than {@code maxElements} items.
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
   * Drop all items except for the last {@code maxElements} items.
   * This operator will buffer up to {@code maxElements} items until it receives the complete
   * signal and then it will send all the buffered items and the complete signal. If less than
   * {@code maxElements} are emitted by the upstream then it is possible for the downstream to receive
   * less than {@code maxElements} items. This method is an alias for the {@link #last(int)} method.
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
   * Drop items from this stream until an item no longer matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, no items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns false, all subsequent
   * items will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
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
   * Drop items from this stream until an item matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, no items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns true, all subsequent
   * items will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
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
   * Return items from this stream until an item fails to match the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns false, the stream will
   * be completed and the upstream canceled. This is equivalent to {@link #takeUntil(Predicate)}
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
   * Return items from this stream until an item matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns true, the stream will
   * be completed and the upstream canceled. This is equivalent to {@link #takeWhile(Predicate)}
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
   * Drops items from the stream if they are equal to the previous item emitted in the stream.
   * The items are tested for equality using the {@link Objects#equals(Object, Object)} method.
   * This method is an alias for {@link #skipConsecutiveDuplicates()}. It is equivalent to invoking
   * {@link #filterSuccessive(SuccessivePredicate)} passing a {@link SuccessivePredicate} filters
   * out successive items that are equal.
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
   * Drops items from the stream if they are equal to the previous item emitted in the stream.
   * The items are tested for equality using the {@link Objects#equals(Object, Object)} method.
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
   * Filter consecutive items emitted by this stream using the specified {@link SuccessivePredicate}.
   * Any candidate items that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other items will be dropped. The predicate passes the last emitted item
   * as well as the candidate item.
   *
   * @param predicate the comparator to determine whether two successive items are equal.
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

  /**
   * Drops items emitted by a stream that are followed by newer items before
   * the given timeout value expires. The timer resets on each emission.
   * This is an alias for {@link #debounce(int)}.
   *
   * @param timeout the timeout.
   * @return the new stream.
   * @see #debounce(int)
   */
  @Nonnull
  default Stream<T> throttleWithTimeout( final int timeout )
  {
    return compose( s -> new ThrottleWithTimeoutOperator<>( s, timeout ) );
  }

  /**
   * Drops items emitted by a stream that are followed by newer items before
   * the given timeout value expires. The timer resets on each emission.
   * This is an alias for {@link #throttleWithTimeout(int)}.
   *
   * @param timeout the timeout.
   * @return the new stream.
   * @see #throttleWithTimeout(int)
   */
  @Nonnull
  default Stream<T> debounce( final int timeout )
  {
    return throttleWithTimeout( timeout );
  }

  /**
   * Transform items emitted by this stream using the {@code mapper} function.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to use to map the items.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> map( @Nonnull final Function<T, DownstreamT> mapper )
  {
    return compose( p -> new MapOperator<>( p, mapper ) );
  }

  /**
   * Transform items emitted by this stream to a constant {@code value}.
   *
   * @param <DownstreamT> the type of the constant value emitted.
   * @param value         the constant value to emit.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> mapTo( final DownstreamT value )
  {
    return map( v -> value );
  }

  /**
   * Map each input item to a stream and then concatenate the items emitted by the mapped stream
   * into this stream. The method operates on a single stream at a time and the result is a concatenation of
   * items emitted from all the streams returned by the mapper function. This method is equivalent to
   * {@link #mergeMap(Function, int)} with a <code>maxConcurrency</code> set to <code>1</code>. This
   * method is also an alias for {@link #concatMap(Function)}.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   * @see #concatMap(Function)
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> flatMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, 1 );
  }

  /**
   * Map each input item to a stream and then concatenate the items emitted by the mapped stream
   * into this stream. The method operates on a single stream at a time and the result is a concatenation of
   * items emitted from all the streams returned by the mapper function. This method is equivalent to
   * {@link #mergeMap(Function, int)} with a <code>maxConcurrency</code> set to <code>1</code>. This
   * method is also an alias for {@link #flatMap(Function)}.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   * @see #flatMap(Function)
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> concatMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return flatMap( mapper );
  }

  /**
   * Map each input item to a stream and then flatten the items emitted by that stream into
   * this stream. The items are merged concurrently up to the maximum concurrency specified by
   * {@code maxConcurrency}. Thus items from different inner streams may be interleaved with other
   * streams that are currently active or subscribed.
   *
   * <p>If an input item is received when the merged stream has already subscribed to the maximum
   * number of inner streams as defined by the <code>maxConcurrency</code> parameter then the extra
   * items are placed on an unbounded buffer. This can lead to significant memory pressure and out
   * of memory conditions if the upstream emits items at a faster rate than the merge stream can
   * complete the inner streams.</p>
   *
   * @param <DownstreamT>  the type of the items that the {@code mapper} function emits.
   * @param mapper         the function to map the items to the inner stream.
   * @param maxConcurrency the maximum number of inner stream that can be subscribed to at one time.
   * @return the new stream.
   * @see #mergeMap(Function)
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper,
                                                      final int maxConcurrency )
  {
    return compose( p -> new MapOperator<>( p, mapper ).compose( o -> new MergeOperator<>( o, maxConcurrency ) ) );
  }

  /**
   * Map each input item to a stream and flatten the items emitted by the inner stream into this stream.
   * The number of streams that can be flattened concurrently is specified by {@link #DEFAULT_MAX_CONCURRENCY}.
   * Invoking this method is equivalent to invoking {@link #mergeMap(Function, int)} and passing the
   * {@link #DEFAULT_MAX_CONCURRENCY} constant as the {@code maxConcurrency} parameter.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, DEFAULT_MAX_CONCURRENCY );
  }

  /**
   * Map each input item to a stream and emit the items from the most recently
   * mapped stream. The stream that the input item is mapped to is the active stream
   * and all items emitted on the active stream are merged into this stream. If the
   * active stream completes then it is no longer the active stream but this stream does
   * not complete. If a new input item is received while there is an active stream is
   * present then the active stream is canceled and the new input item is mapped to a
   * new stream that is made active.
   *
   * @param <DownstreamT> the type of the items that this stream emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> switchMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return compose( p -> new MapOperator<>( p, mapper ).compose( SwitchOperator::new ) );
  }

  /**
   * Map each input item to a stream and emit the items from the most recently
   * mapped stream. The stream that the input item is mapped to is the active stream
   * and all items emitted on the active stream are merged into this stream. If the
   * active stream completes then it is no longer the active stream but this stream does
   * not complete. If a new input item is received while there is an active stream is
   * present then the active stream is canceled and the new input item is mapped to a
   * new stream that is made active.
   *
   * @param <DownstreamT> the type of the items that this stream emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> exhaustMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return compose( p -> new MapOperator<>( p, mapper ).compose( ExhaustOperator::new ) );
  }

  /**
   * Emit all the items from this stream and then when the complete signal is emitted then
   * merge the items from the specified streams one after another until all streams complete.
   *
   * @param streams the streams to append to this stream.
   * @return the new stream.
   * @see #prepend(Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Stream<T> append( @Nonnull final Stream<T>... streams )
  {
    final ArrayList<Stream<T>> s = new ArrayList<>( streams.length + 1 );
    s.add( this );
    Collections.addAll( s, streams );
    return compose( p -> Streak.fromCollection( s ).compose( o -> new MergeOperator<>( o, 1 ) ) );
  }

  /**
   * Merge the items from the specified streams before the items from this stream sequentially.
   * For each of the supplied streams, emit all items from the stream until it completes an then move
   * to the next stream. If no more streams have been supplied then emit the items from this stream.
   *
   * @param streams the stream to prepend to this stream.
   * @return the new stream.
   * @see #prepend(Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Stream<T> prepend( @Nonnull final Stream<T>... streams )
  {
    final ArrayList<Stream<T>> s = new ArrayList<>( streams.length + 1 );
    Collections.addAll( s, streams );
    s.add( this );
    return compose( p -> Streak.fromCollection( s ).compose( o -> new MergeOperator<>( o, 1 ) ) );
  }

  /**
   * Emit the specified item before emitting items from this stream.
   *
   * @param value the initial value to emit.
   * @return the new stream.
   * @see #prepend(Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Stream<T> startWith( @Nonnull final T value )
  {
    return prepend( Streak.of( value ) );
  }

  /**
   * Emit the specified item after emitting items from this stream.
   *
   * @param value the last value to emit.
   * @return the new stream.
   * @see #append(Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Stream<T> endWith( @Nonnull final T value )
  {
    return append( Streak.of( value ) );
  }

  /**
   * Apply an accumulator function to each item in the stream emit the accumulated value.
   *
   * @param <DownstreamT>       the type of the items that the {@code accumulatorFunction} function emits.
   * @param accumulatorFunction the function to use to accumulate the values.
   * @param initialValue        the initial value to begin accumulation from.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Stream<DownstreamT> scan( @Nonnull final AccumulatorFunction<T, DownstreamT> accumulatorFunction,
                                                  @Nonnull final DownstreamT initialValue )
  {
    return compose( p -> new ScanOperator<>( p, accumulatorFunction, initialValue ) );
  }

  /**
   * If upstream emits no items and then completes then emit the {@code defaultValue} before completing this stream.
   *
   * @param defaultValue the default value to emit if upstream completes and is empty.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> defaultIfEmpty( @Nonnull final T defaultValue )
  {
    return compose( p -> new DefaultIfEmptyOperator<>( p, defaultValue ) );
  }

  /**
   * Signals error with a {@link TimeoutException} if an item is not emitted within the specified timeout period from the previous item.
   *
   * @param timeoutTime the timeout period after which the stream is terminated.
   * @return the new stream.
   */
  @Nonnull
  default Stream<T> timeout( final int timeoutTime )
  {
    return compose( p -> new TimeoutOperator<>( p, timeoutTime ) );
  }

  default void forEach( @Nonnull final Consumer<T> action )
  {
    terminate( () -> new ForEachSubscriber<>( action ) );
  }

  default void terminate( @Nonnull final Supplier<Subscriber<T>> terminateFunction )
  {
    this.subscribe( new ValidatingSubscriber<>( terminateFunction.get() ) );
  }

  /**
   * Compost this stream with another stream and return the new stream.
   * This method is used to compose chains of stream operations.
   *
   * @param <DownstreamT> the type of the item emitted by downstream stage.
   * @param <S>           the type of the downstream stage.
   * @param function      the function used to compose stream operations.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT, S extends Stream<DownstreamT>> S compose( @Nonnull final Function<Stream<T>, S> function )
  {
    return function.apply( new ValidatingStream<>( this ) );
  }
}
