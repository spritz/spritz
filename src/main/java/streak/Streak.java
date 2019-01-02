package streak;

public final class Streak
{
  private static final StreakContext CONTEXT = new StreakContext();

  private Streak()
  {
  }

  public static boolean areNamesEnabled()
  {
    //TODO: Convert this into compile-time constraint.
    return true;
  }

  public static boolean shouldCheckInvariants()
  {
    //TODO: Convert this into compile-time constraint.
    return true;
  }

  public static boolean shouldCheckApiInvariants()
  {
    //TODO: Convert this into compile-time constraint.
    return true;
  }

  /**
   * Return the current Streak context.
   *
   * @return the current Streak context.
   */
  public static StreakContext context()
  {
    return CONTEXT;
  }
}
