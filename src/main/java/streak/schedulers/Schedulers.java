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
}
