package spritz;

import org.realityforge.braincheck.BrainCheckConfig;
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

  public static boolean purgeTasksWhenRunawayDetected()
  {
    return SpritzConfig.purgeTasksWhenRunawayDetected();
  }
}
