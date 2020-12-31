package spritz;

/**
 * Location of all compile time configuration settings for framework.
 */
@SuppressWarnings( "FieldMayBeFinal" )
final class SpritzConfig
{
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean PRODUCTION_MODE = PROVIDER.isProductionMode();
  private static boolean ENABLE_NAMES = PROVIDER.areNamesEnabled();
  private static boolean VALIDATE_SUBSCRIPTIONS = PROVIDER.shouldValidateSubscriptions();
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

  static boolean areNamesEnabled()
  {
    return ENABLE_NAMES;
  }

  static boolean shouldValidateSubscriptions()
  {
    return VALIDATE_SUBSCRIPTIONS;
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

    String loggerType()
    {
      /*
       * Valid values are: "none", "console" and "proxy" (for testing)
       */
      return System.getProperty( "spritz.logger" );
    }
  }
}
