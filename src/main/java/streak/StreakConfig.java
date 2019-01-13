package streak;

/**
 * Location of all compile time configuration settings for framework.
 */
final class StreakConfig
{
  private static final ConfigProvider PROVIDER = new ConfigProvider();
  private static final boolean PRODUCTION_MODE = PROVIDER.isProductionMode();
  private static boolean VALIDATE_SUBSCRIPTIONS = PROVIDER.shouldValidateSubscriptions();

  private StreakConfig()
  {
  }

  static boolean isProductionMode()
  {
    return PRODUCTION_MODE;
  }

  static boolean shouldValidateSubscriptions()
  {
    return VALIDATE_SUBSCRIPTIONS;
  }

  private static final class ConfigProvider
    extends AbstractConfigProvider
  {
    @GwtIncompatible
    @Override
    boolean isProductionMode()
    {
      return "production".equals( System.getProperty( "streak.environment", "production" ) );
    }

    @GwtIncompatible
    @Override
    boolean shouldValidateSubscriptions()
    {
      return "true".equals( System.getProperty( "streak.validate_subscriptions",
                                                isProductionMode() ? "false" : "true" ) );
    }
  }

  @SuppressWarnings( { "unused", "StringEquality" } )
  private static abstract class AbstractConfigProvider
  {
    boolean isProductionMode()
    {
      return "production" == System.getProperty( "streak.environment" );
    }

    boolean shouldValidateSubscriptions()
    {
      return "true" == System.getProperty( "streak.validate_subscriptions" );
    }
  }
}
