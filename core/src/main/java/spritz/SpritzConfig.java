package spritz;

/**
 * Location of all compile time configuration settings for framework.
 */
final class SpritzConfig
{
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean PRODUCTION_MODE = PROVIDER.isProductionMode();
  private static boolean ENABLE_NAMES = PROVIDER.areNamesEnabled();
  private static boolean VALIDATE_SUBSCRIPTIONS = PROVIDER.shouldValidateSubscriptions();
  private static boolean PURGE_ON_RUNAWAY = PROVIDER.purgeTasksWhenRunawayDetected();
  private static boolean UNCAUGHT_ERROR_HANDLERS = PROVIDER.areUncaughtErrorHandlersEnabled();
  private static final String LOGGER_TYPE = PROVIDER.loggerType();

  private SpritzConfig()
  {
  }

  static boolean isDevelopmentMode()
  {
    return !isProductionMode();
  }

  static boolean isProductionMode()
  {
    return PRODUCTION_MODE;
  }

  static boolean isJvm()
  {
    return true;
  }

  static boolean purgeTasksWhenRunawayDetected()
  {
    return PURGE_ON_RUNAWAY;
  }

  static boolean areNamesEnabled()
  {
    return ENABLE_NAMES;
  }

  static boolean shouldValidateSubscriptions()
  {
    return VALIDATE_SUBSCRIPTIONS;
  }

  static boolean areUncaughtErrorHandlersEnabled()
  {
    return UNCAUGHT_ERROR_HANDLERS;
  }

  static String loggerType()
  {
    return LOGGER_TYPE;
  }

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean isProductionMode()
    {
      return "production".equals( System.getProperty( "spritz.environment", "production" ) );
    }

    @GwtIncompatible
    @Override
    boolean areNamesEnabled()
    {
      return "true".equals( System.getProperty( "spritz.enable_names", isProductionMode() ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldValidateSubscriptions()
    {
      return "true".equals( System.getProperty( "spritz.validate_subscriptions",
                                                isProductionMode() ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean purgeTasksWhenRunawayDetected()
    {
      return "true".equals( System.getProperty( "spritz.purge_tasks_when_runaway_detected", "true" ) );
    }

    @GwtIncompatible
    @Override
    boolean areUncaughtErrorHandlersEnabled()
    {
      return "true".equals( System.getProperty( "spritz.enable_uncaught_error_handlers",
                                                PRODUCTION_MODE ? "false" : "true" ) );
    }

    @GwtIncompatible
    @Override
    String loggerType()
    {
      return System.getProperty( "spritz.logger", PRODUCTION_MODE ? "basic" : "proxy" );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionMode()
    {
      return "production" == System.getProperty( "spritz.environment" );
    }

    boolean areNamesEnabled()
    {
      return "true" == System.getProperty( "spritz.enable_names" );
    }

    boolean shouldValidateSubscriptions()
    {
      return "true" == System.getProperty( "spritz.validate_subscriptions" );
    }

    boolean purgeTasksWhenRunawayDetected()
    {
      return "true" == System.getProperty( "spritz.purge_tasks_when_runaway_detected" );
    }

    boolean areUncaughtErrorHandlersEnabled()
    {
      return "true" == System.getProperty( "spritz.enable_uncaught_error_handlers" );
    }

    String loggerType()
    {
      /*
       * Valid values are: "none", "basic", "jul" (java.util.logging) and "proxy" (for testing)
       */
      return System.getProperty( "spritz.logger" );
    }
  }
}
