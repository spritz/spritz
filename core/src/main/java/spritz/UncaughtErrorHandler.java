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
   * @param error  the exception.
   */
  void onUncaughtError( @Nonnull Throwable error );
}
