package spritz;

import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Provide access to global configuration settings.
 */
@MetaDataSource
public final class Spritz
{
  private Spritz()
  {
  }

  /**
   * Return true if user should pass names into API methods, false if should pass null.
   *
   * @return true if user should pass names into API methods, false if should pass null.
   */
  public static boolean areNamesEnabled()
  {
    return SpritzConfig.areNamesEnabled();
  }

  /**
   * Return true if subscription protocol will be validated.
   *
   * @return true if subscription protocol will be validated.
   */
  public static boolean shouldValidateSubscriptions()
  {
    return SpritzConfig.shouldValidateSubscriptions();
  }

  /**
   * Return true if uncaught error handlers are enabled.
   *
   * @return true if uncaught error handlers are enabled, false otherwise.
   */
  public static boolean areUncaughtErrorHandlersEnabled()
  {
    return SpritzConfig.areUncaughtErrorHandlersEnabled();
  }

  /**
   * Return true if invariants will be checked.
   *
   * @return true if invariants will be checked.
   */
  public static boolean shouldCheckInvariants()
  {
    return BrainCheckConfig.checkInvariants();
  }

  /**
   * Return true if apiInvariants will be checked.
   *
   * @return true if apiInvariants will be checked.
   */
  public static boolean shouldCheckApiInvariants()
  {
    return BrainCheckConfig.checkApiInvariants();
  }

  /**
   * Return true if active tasks will be purged if the scheduler is still running after the maximum number of rounds.
   *
   * @return true if active tasks will be purged if the scheduler is still running after the maximum number of rounds.
   */
  public static boolean purgeTasksWhenRunawayDetected()
  {
    return SpritzConfig.purgeTasksWhenRunawayDetected();
  }

  /**
   * Add error handler to the list of error handlers called.
   * The handler should not already be in the list. This method should NOT be called if
   * {@link #areUncaughtErrorHandlersEnabled()} returns false.
   *
   * @param handler the error handler.
   */
  public static void addUncaughtErrorHandler( @Nonnull final UncaughtErrorHandler handler )
  {
    UncaughtErrorHandlerSupport.get().addUncaughtErrorHandler( handler );
  }

  /**
   * Remove error handler from list of existing error handlers.
   * The handler should already be in the list. This method should NOT be called if
   * {@link #areUncaughtErrorHandlersEnabled()} returns false.
   *
   * @param handler the error handler.
   */
  public static void removeUncaughtErrorHandler( @Nonnull final UncaughtErrorHandler handler )
  {
    UncaughtErrorHandlerSupport.get().removeUncaughtErrorHandler( handler );
  }

  /**
   * Report an uncaught error in stream.
   *
   * @param error  the error.
   */
  public static void reportUncaughtError( @Nonnull final Throwable error )
  {
    if ( areUncaughtErrorHandlersEnabled() )
    {
      UncaughtErrorHandlerSupport.get().onUncaughtError( error );
    }
  }
}
