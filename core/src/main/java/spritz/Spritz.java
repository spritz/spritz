package spritz;

import javax.annotation.Nonnull;
import spritz.internal.annotations.MetaDataSource;
import spritz.schedulers.Scheduler;

@MetaDataSource
public final class Spritz
{
  private Spritz()
  {
  }

  public static boolean shouldValidateSubscriptions()
  {
    return SpritzConfig.shouldValidateSubscriptions();
  }

  public static boolean purgeTasksWhenRunawayDetected()
  {
    return false;
  }

  /**
   * Return the spritz scheduler.
   *
   * @return the spritz scheduler.
   */
  @Nonnull
  public static Scheduler scheduler()
  {
    return SchedulerHolder.scheduler();
  }

  /**
   * Shutdown the spritz scheduler and cancel any inactive tasks.
   */
  public static void shutdown()
  {
    SchedulerHolder.shutdown();
  }
}
