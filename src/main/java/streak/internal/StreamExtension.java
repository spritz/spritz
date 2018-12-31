package streak.internal;

import java.util.function.Function;
import javax.annotation.Nonnull;
import streak.Flow;

/**
 * Base interface used to define stream extensions.
 *
 * @param <T> The type of the elements that the stream consumes.
 */
public interface StreamExtension<T>
{
  /**
   * Compost this stream with another stream and return the new stream.
   * This method is used to compose chains of stream operations.
   *
   * @param <DownstreamT> the type of element emitted by downstream stage.
   * @param <S> the type of the downstream stage.
   * @param function the function used to compose stream operations.
   * @return the new stream.
   */
  @Nonnull
  default <DownstreamT, S extends Flow.Stream<DownstreamT>> S compose( @Nonnull final Function<Flow.Stream<T>, S> function )
  {
    return function.apply( new ValidatingStream<>( self() ) );
  }

  /**
   * Return the reference to the stream that the extension is extending.
   *
   * @return the underlying stream.
   */
  @Nonnull
  Flow.Stream<T> self();
}
