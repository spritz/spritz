package spritz;

import org.realityforge.braincheck.BrainCheckConfig;
import spritz.internal.annotations.MetaDataSource;

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
}
