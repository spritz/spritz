package streak.internal.transforming;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;
import streak.internal.StreamExtension;

/**
 * Operators for transforming elements from one form to another.
 *
 * @param <T> the type of the elements that the stream consumes.
 */
public interface TransformingOperators<T>
  extends StreamExtension<T>
{
  /**
   * The maximum concurrency of {@link #mergeMap(Function)} operator that does not specify concurrency.
   * This value is high enough that it is expected to be effectively infinite while not causing numeric
   * overflow in either JS or java compile targets.
   */
  int DEFAULT_MAX_CONCURRENCY = 1024 * 1024;

  /**
   * Transform elements emitted by this stream using the {@code mapper} function.
   *
   * @param <DownstreamT> the type of the elements that the {@code mapper} function emits.
   * @param mapper        the function to use to map the elements.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> map( @Nonnull final Function<T, DownstreamT> mapper )
  {
    return compose( p -> new MapOperator<>( p, mapper ) );
  }

  /**
   * Transform elements emitted by this stream to a constant {@code value}.
   *
   * @param <DownstreamT> the type of the constant value emitted.
   * @param value         the constant value to emit.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> mapTo( final DownstreamT value )
  {
    return map( v -> value );
  }

  /**
   * Map each input element to a stream and then concatenate the elements emitted by the mapped stream
   * into this stream. The method operates on a single stream at a time and the result is a concatenation of
   * elements emitted from all the streams produced by the mapper function. This method is equivalent to
   * {@link #mergeMap(Function, int)} with a <code>maxConcurrency</code> set to <code>1</code>. This
   * method is also an alias for {@link #concatMap(Function)}.
   *
   * @param <DownstreamT> the type of the elements that the {@code mapper} function emits.
   * @param mapper        the function to map the elements to the inner stream.
   * @return the new stream.
   * @see #concatMap(Function)
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> flatMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, 1 );
  }

  /**
   * Map each input element to a stream and then concatenate the elements emitted by the mapped stream
   * into this stream. The method operates on a single stream at a time and the result is a concatenation of
   * elements emitted from all the streams produced by the mapper function. This method is equivalent to
   * {@link #mergeMap(Function, int)} with a <code>maxConcurrency</code> set to <code>1</code>. This
   * method is also an alias for {@link #flatMap(Function)}.
   *
   * @param <DownstreamT> the type of the elements that the {@code mapper} function emits.
   * @param mapper        the function to map the elements to the inner stream.
   * @return the new stream.
   * @see #flatMap(Function)
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> concatMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper )
  {
    return flatMap( mapper );
  }

  /**
   * Map each input element to a stream and then flatten the elements emitted by that stream into
   * this stream. The elements are merged concurrently up to the maximum concurrency specified by
   * {@code maxConcurrency}. Thus elements from different inner streams may be interleaved with other
   * streams that are currently active or subscribed.
   *
   * <p>If an input element is received when the merged stream has already subscribed to the maximum
   * number of inner streams as defined by the <code>maxConcurrency</code> parameter then the extra
   * elements are placed on an unbounded buffer. This can lead to significant memory pressure and out
   * of memory conditions if the upstream produces elements at a faster rate than the merge stream can
   * complete the inner streams.</p>
   *
   * @param <DownstreamT>  the type of the elements that the {@code mapper} function emits.
   * @param mapper         the function to map the elements to the inner stream.
   * @param maxConcurrency the maximum number of inner stream that can be subscribed to at one time.
   * @return the new stream.
   * @see #mergeMap(Function)
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper,
                                                           final int maxConcurrency )
  {
    return compose( p -> new MapOperator<>( p, mapper ).compose( o -> new MergeOperator<>( o, maxConcurrency ) ) );
  }

  /**
   * Map each input element to a stream and flatten the elements produced by the inner stream into this stream.
   * The number of streams that can be flattened concurrently is specified by {@link #DEFAULT_MAX_CONCURRENCY}.
   * Invoking this method is equivalent to invoking {@link #mergeMap(Function, int)} and passing the
   * {@link #DEFAULT_MAX_CONCURRENCY} constant as the {@code maxConcurrency} parameter.
   *
   * @param <DownstreamT> the type of the elements that the {@code mapper} function emits.
   * @param mapper        the function to map the elements to the inner stream.
   * @return the new stream.
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, DEFAULT_MAX_CONCURRENCY );
  }

  /**
   * Map each input element to a stream and emit the elements from the most recently
   * mapped stream. The stream that the input element is mapped to is the active stream
   * and all elements emitted on the active stream are merged into this stream. If the
   * active stream completes then it is no longer the active stream but this stream does
   * not complete. If a new input element is received while there is an active stream is
   * present then the active stream is disposed and the new input element is mapped to a
   * new stream that is made active.
   *
   * @param <DownstreamT> the type of the elements that this stream emits.
   * @param mapper        the function to map the elements to the inner stream.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> switchMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper )
  {
    return compose( p -> new MapOperator<>( p, mapper ).compose( SwitchOperator::new ) );
  }

  /**
   * Map each input element to a stream and emit the elements from the most recently
   * mapped stream. The stream that the input element is mapped to is the active stream
   * and all elements emitted on the active stream are merged into this stream. If the
   * active stream completes then it is no longer the active stream but this stream does
   * not complete. If a new input element is received while there is an active stream is
   * present then the active stream is disposed and the new input element is mapped to a
   * new stream that is made active.
   *
   * @param <DownstreamT> the type of the elements that this stream emits.
   * @param mapper        the function to map the elements to the inner stream.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> exhaustMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper )
  {
    return compose( p -> new MapOperator<>( p, mapper ).compose( ExhaustOperator::new ) );
  }

  /**
   * Emit all the elements from this stream and then when the complete signal is emitted then
   * merge the elements from the specified streams one after another until all streams complete.
   *
   * @param streams the streams to append to this stream.
   * @return the new stream.
   * @see #prepend(Flow.Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Flow.Stream<T> append( @Nonnull final Flow.Stream<T>... streams )
  {
    final ArrayList<Flow.Stream<T>> s = new ArrayList<>( streams.length + 1 );
    s.add( self() );
    Collections.addAll( s, streams );
    return compose( p -> Streak.context().fromCollection( s ).compose( o -> new MergeOperator<>( o, 1 ) ) );
  }

  /**
   * Merge the elements from the specified streams before the elements from this stream sequentially.
   * For each of the supplied streams, emit all elements from the stream until it completes an then move
   * to the next stream. If no more streams have been supplied then emit the elements from this stream.
   *
   * @param streams the stream to prepend to this stream.
   * @return the new stream.
   * @see #prepend(Flow.Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Flow.Stream<T> prepend( @Nonnull final Flow.Stream<T>... streams )
  {
    final ArrayList<Flow.Stream<T>> s = new ArrayList<>( streams.length + 1 );
    Collections.addAll( s, streams );
    s.add( self() );
    return compose( p -> Streak.context().fromCollection( s ).compose( o -> new MergeOperator<>( o, 1 ) ) );
  }

  /**
   * Emit the specified element before emitting elements from this stream.
   *
   * @param value the initial value to emit.
   * @return the new stream.
   * @see #prepend(Flow.Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Flow.Stream<T> startWith( @Nonnull final T value )
  {
    return prepend( Streak.context().of( value ) );
  }

  /**
   * Emit the specified element after emitting elements from this stream.
   *
   * @param value the last value to emit.
   * @return the new stream.
   * @see #append(Flow.Stream[])
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  default Flow.Stream<T> endWith( @Nonnull final T value )
  {
    return append( Streak.context().of( value ) );
  }

  /**
   * Apply an accumulator function to each element in the stream emit the accumulated value.
   *
   * @param <DownstreamT>       the type of the elements that the {@code accumulatorFunction} function emits.
   * @param accumulatorFunction the function to use to accumulate the values.
   * @param initialValue        the initial value to begin accumulation from.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> scan( @Nonnull final AccumulatorFunction<T, DownstreamT> accumulatorFunction,
                                                       @Nonnull final DownstreamT initialValue )
  {
    return compose( p -> new ScanOperator<>( p, accumulatorFunction, initialValue ) );
  }
}
