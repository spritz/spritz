package streak.schedulers;

import arez.Disposable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

final class BasicScheduler
  implements Scheduler, Disposable
{
  private final long _schedulerStart = System.currentTimeMillis();
  private final ScheduledExecutorService _executorService = new ScheduledThreadPoolExecutor( 100 );

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose()
  {
    _executorService.shutdown();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDisposed()
  {
    return _executorService.isShutdown();
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
  public Disposable schedule( @Nonnull final Runnable task,
                              final int initialDelay,
                              final int period )
  {
    final ScheduledFuture<?> future = _executorService.schedule( task, now() + initialDelay, TimeUnit.MILLISECONDS );
    return new Disposable()
    {
      @Override
      public void dispose()
      {
        future.cancel( true );
      }

      @Override
      public boolean isDisposed()
      {
        return future.isCancelled();
      }
    };
  }
}
