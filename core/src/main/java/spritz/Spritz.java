package spritz;

import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public final class Spritz
{
  private Spritz()
  {
  }

  public static boolean areNamesEnabled()
  {
    return SpritzConfig.areNamesEnabled();
  }

  public static boolean shouldValidateSubscriptions()
  {
    return SpritzConfig.shouldValidateSubscriptions();
  }

  public static boolean purgeTasksWhenRunawayDetected()
  {
    return SpritzConfig.purgeTasksWhenRunawayDetected();
  }
}
