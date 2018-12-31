package streak.internal.transforming;

import java.util.function.Function;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.StreamExtension;

/**
 * Operators for filtering elements from a stream.
 *
 * @param <T> The type of the elements that the stream consumes and emits.
 */
public interface TransformingOperators<T>
  extends StreamExtension<T>
{
  @Nonnull
  default <DownstreamT> Flow.Stream<DownstreamT> map( @Nonnull final Function<T, DownstreamT> transform )
  {
    return compose( p -> new MapPublisher<>( p, transform ) );
  }
}
