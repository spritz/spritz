package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Abstract stream implementation for common scenario where there is an upstream stage.
 */
abstract class AbstractStream<UpstreamT, DownstreamT>
  extends Stream<DownstreamT>
{
  /**
   * The upstream stream stage.
   */
  @Nonnull
  private final Stream<UpstreamT> _upstream;

  AbstractStream( @Nullable final String name, @Nonnull final Stream<UpstreamT> upstream )
  {
    super( name );
    _upstream = Objects.requireNonNull( upstream );
  }

  /**
   * Create a stream with specified upstream.
   *
   * @param upstream the upstream stream.
   */
  protected AbstractStream( @Nonnull final Stream<UpstreamT> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  /**
   * Return the upstream stream.
   *
   * @return the upstream stream.
   */
  @Nonnull
  protected final Stream<UpstreamT> getUpstream()
  {
    return _upstream;
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  final String getQualifiedName()
  {
    return getUpstream().getQualifiedName() + "." + getName();
  }
}
