package spritz;

import java.lang.reflect.Field;
import javax.annotation.Nonnull;
import spritz.internal.annotations.GwtIncompatible;

/**
 * Utility class for interacting with Spritz config settings in tests.
 */
@GwtIncompatible
public final class SpritzTestUtil
{
  private SpritzTestUtil()
  {
  }

  /**
   * Reset the state of spritz config to either production or development state.
   *
   * @param productionMode true to set it to production mode configuration, false to set it to development mode config.
   */
  @SuppressWarnings( "ConstantConditions" )
  public static void resetConfig( final boolean productionMode )
  {
    if ( SpritzConfig.isProductionMode() )
    {
      /*
       * This should really never happen but if it does add assertion (so code stops in debugger) or
       * failing that throw an exception.
       */
      assert !SpritzConfig.isProductionMode();
      throw new IllegalStateException( "Unable to reset config as Spritz is in production mode" );
    }

    if ( productionMode )
    {
      disableNames();
      noValidateSubscriptions();
    }
    else
    {
      enableNames();
      validateSubscriptions();
    }
    purgeTasksWhenRunawayDetected();
    resetState();
  }

  /**
   * Reset the state of Spritz.
   * This occasionally needs to be invoked after changing configuration settings in tests.
   */
  private static void resetState()
  {
    ( (SpritzLogger.ProxyLogger) SpritzLogger.getLogger() ).setLogger( null );
    TemporalScheduler.reset();
    UncaughtErrorHandlerSupport.reset();
  }

  /**
   * Set `spritz.enable_names` setting to true.
   */
  public static void enableNames()
  {
    setEnableNames( true );
  }

  /**
   * Set `spritz.enable_names` setting to false.
   */
  public static void disableNames()
  {
    setEnableNames( false );
  }

  /**
   * Configure the `spritz.enable_names` setting.
   *
   * @param value the setting.
   */
  private static void setEnableNames( final boolean value )
  {
    setConstant( "ENABLE_NAMES", value );
  }

  /**
   * Set `spritz.purge_tasks_when_runaway_detected` setting to true.
   */
  public static void purgeTasksWhenRunawayDetected()
  {
    setPurgeTasksWhenRunawayDetected( true );
  }

  /**
   * Set `spritz.purge_tasks_when_runaway_detected` setting to false.
   */
  public static void noPurgeTasksWhenRunawayDetected()
  {
    setPurgeTasksWhenRunawayDetected( false );
  }

  /**
   * Configure the `spritz.purge_tasks_when_runaway_detected` setting.
   *
   * @param value the setting.
   */
  private static void setPurgeTasksWhenRunawayDetected( final boolean value )
  {
    setConstant( "PURGE_ON_RUNAWAY", value );
  }

  /**
   * Set `spritz.validate_subscriptions` setting to true.
   */
  public static void validateSubscriptions()
  {
    setValidateSubscriptions( true );
  }

  /**
   * Set the `spritz.validate_subscriptions` setting to false.
   */
  public static void noValidateSubscriptions()
  {
    setValidateSubscriptions( false );
  }

  /**
   * Configure the `spritz.validate_subscriptions` setting.
   *
   * @param validateSubscriptions the "validate_subscriptions" setting.
   */
  private static void setValidateSubscriptions( final boolean validateSubscriptions )
  {
    setConstant( "VALIDATE_SUBSCRIPTIONS", validateSubscriptions );
  }

  /**
   * Set the specified field name on SpritzConfig.
   */
  @SuppressWarnings( "ConstantConditions" )
  private static void setConstant( @Nonnull final String fieldName, final boolean value )
  {
    if ( !SpritzConfig.isProductionMode() )
    {
      try
      {
        final Field field = SpritzConfig.class.getDeclaredField( fieldName );
        field.setAccessible( true );
        field.set( null, value );
      }
      catch ( final NoSuchFieldException | IllegalAccessException e )
      {
        throw new IllegalStateException( "Unable to change constant " + fieldName, e );
      }
    }
    else
    {
      /*
       * This should not happen but if it does then just fail with an assertion or error.
       */
      assert !SpritzConfig.isProductionMode();
      throw new IllegalStateException( "Unable to change constant " + fieldName + " as Spritz is in production mode" );
    }
  }
}
