package spritz;

import javax.annotation.Nonnull;

/**
 * Interface defining handler invoked when an unexpected error occurs.
 */
@FunctionalInterface
public interface UncaughtErrorHandler
{
  /**
   * Callback invoked when an unexpected error occurs.
   *
   * @param stream the stream that caught the exception.
   * @param error  the exception.
   */
  void onUncaughtError( @Nonnull Stream<?> stream, @Nonnull Throwable error );
}
