package streak.schedulers;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

final class BasicScheduler
  implements Scheduler
{
  private final long _schedulerStart = System.currentTimeMillis();
  private final ScheduledExecutorService _executorService = new ScheduledThreadPoolExecutor( 1 );

  /**
   * {@inheritDoc}
   */
  @Override
  public void shutdown()
  {
    _executorService.shutdown();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int now()
  {
    return (int) ( System.currentTimeMillis() - _schedulerStart );
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public SchedulerTask schedule( @Nonnull final Runnable task,
                                 final int initialDelay,
                                 final int period )
  {
    final ScheduledFuture<?> future =
      0 == period ?
      _executorService.schedule( task, initialDelay, TimeUnit.MILLISECONDS ) :
      _executorService.scheduleAtFixedRate( task, initialDelay, period, TimeUnit.MILLISECONDS );
    return () -> future.cancel( true );
  }
}
