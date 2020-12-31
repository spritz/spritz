package spritz;

import org.realityforge.braincheck.BrainCheckConfig;

/**
 * Provide access to global configuration settings.
 */
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
}
