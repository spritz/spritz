package streak;

import java.util.function.Function;
import javax.annotation.Nonnull;

public interface BasePublisherExtension<UpstreamT>
{
  @Nonnull
  default <DownstreamT, R extends BasePublisherExtension<DownstreamT>> R compose( @Nonnull final Function<Flow.Publisher<UpstreamT>, R> composeFunction )
  {
    return composeFunction.apply( new ValidatingPublisher<>( self() ) );
  }

  @Nonnull
  Flow.Publisher<UpstreamT> self();
}
