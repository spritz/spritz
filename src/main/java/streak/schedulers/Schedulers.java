package streak.schedulers;

import javax.annotation.Nonnull;

public final class Schedulers
{
  @Nonnull
  private static Scheduler c_scheduler = new BasicScheduler();

  private Schedulers()
  {
  }

  @Nonnull
  public static Scheduler current()
  {
    return c_scheduler;
  }

  public static void reset()
  {
    c_scheduler = new BasicScheduler();
  }

  public static void shutdown()
  {
    c_scheduler.shutdown();
    c_scheduler = new BasicScheduler();
  }
}
