package streak.internal.transforming;

import java.util.function.Function;
import javax.annotation.Nonnull;
import streak.Flow;
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
   * @param <DownstreamT> the type of the elements that the {@code mapper} function emits.
   * @param mapper        the function to map the elements to the inner stream.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper,
                                                           final int maxConcurrency )
  {
    return compose( p -> new MapOperator<>( p, mapper ).compose( o -> new MergeOperator<>( o, maxConcurrency ) ) );
  }

  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Flow.Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, 1024 * 1024 );
  }
}
