package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract stream implementation for common scenario where there is an upstream stage.
 */
abstract class AbstractStream<T>
  extends Stream<T>
{
  /**
   * The upstream stream stage.
   */
  @Nonnull
  private final Publisher<T> _upstream;

  /**
   * Create a stream with specified upstream.
   *
   * @param upstream the upstream stream.
   */
  protected AbstractStream( @Nonnull final Publisher<T> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  /**
   * Return the upstream stream.
   *
   * @return the upstream stream.
   */
  @Nonnull
  protected final Publisher<T> getUpstream()
  {
    return _upstream;
  }
}
