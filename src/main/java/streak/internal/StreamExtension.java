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
  @Nonnull
  default <DownstreamT, S extends Flow.Stream<DownstreamT>> S compose( @Nonnull final Function<Flow.Stream<T>, S> composeFunction )
  {
    return composeFunction.apply( new ValidatingStream<>( self() ) );
  }

  @Nonnull
  Flow.Stream<T> self();
}
