package spritz;

import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.*;

/**
 * Class supporting the propagation of errors for UncaughtErrorHandler callback to multiple error handlers.
 */
final class UncaughtErrorHandlerSupport
  implements UncaughtErrorHandler
{
  private static UncaughtErrorHandlerSupport INSTANCE = new UncaughtErrorHandlerSupport();
  /**
   * The list of error handlers to call when an error is received.
   */
  private final ArrayList<UncaughtErrorHandler> _errorHandlers = new ArrayList<>();

  static UncaughtErrorHandlerSupport get()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      invariant( Spritz::areUncaughtErrorHandlersEnabled,
                 () -> "Spritz-0182: UncaughtErrorHandlerSupport.get() invoked when Spritz.areUncaughtErrorHandlersEnabled() returns false." );
    }
    return INSTANCE;
  }

  static void reset()
  {
    INSTANCE = new UncaughtErrorHandlerSupport();
  }

  private UncaughtErrorHandlerSupport()
  {
  }

  /**
   * Add error handler to the list of error handlers called.
   * The handler should not already be in the list.
   *
   * @param handler the error handler.
   */
  void addUncaughtErrorHandler( @Nonnull final UncaughtErrorHandler handler )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !_errorHandlers.contains( handler ),
                    () -> "Spritz-0096: Attempting to add handler " + handler + " that is already in " +
                          "the list of error handlers." );
    }
    _errorHandlers.add( Objects.requireNonNull( handler ) );
  }

  /**
   * Remove error handler from list of existing error handlers.
   * The handler should already be in the list.
   *
   * @param handler the error handler.
   */
  void removeUncaughtErrorHandler( @Nonnull final UncaughtErrorHandler handler )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> _errorHandlers.contains( handler ),
                    () -> "Spritz-0097: Attempting to remove handler " + handler + " that is not in " +
                          "the list of error handlers." );
    }
    _errorHandlers.remove( Objects.requireNonNull( handler ) );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onUncaughtError( @Nonnull final Throwable error )
  {
    for ( final UncaughtErrorHandler errorHandler : _errorHandlers )
    {
      try
      {
        errorHandler.onUncaughtError( error );
      }
      catch ( final Throwable nestedError )
      {
        if ( Spritz.areNamesEnabled() && BrainCheckConfig.verboseErrorMessages() )
        {
          final String message =
            SpritzUtil.safeGetString( () -> "Exception when notifying error handler '" + errorHandler +
                                            "' of '" + error + "' error." );
          SpritzLogger.log( message, nestedError );
        }
        else
        {
          SpritzLogger.log( "Error triggered when invoking UncaughtErrorHandler.onUncaughtError()", nestedError );
        }
      }
    }
  }
}
