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
}
