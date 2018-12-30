package streak;

import java.util.function.Function;
import javax.annotation.Nonnull;

public interface StreamExtension<UpstreamT>
{
  @Nonnull
  default <DownstreamT, S extends Flow.Stream<DownstreamT>> S compose( @Nonnull final Function<Flow.Stream<UpstreamT>, S> composeFunction )
  {
    return composeFunction.apply( new ValidatingPublisher<>( self() ) );
  }

  @Nonnull
  Flow.Stream<UpstreamT> self();
}
