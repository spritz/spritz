package spritz;

import elemental2.dom.DomGlobal;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import spritz.internal.annotations.GwtIncompatible;
import spritz.schedulers.Scheduler;
import spritz.schedulers.SchedulerTask;

final class SchedulerHolder
{
  @Nonnull
  private static Scheduler c_scheduler = new SchedulerImpl();

  private SchedulerHolder()
  {
  }

  @Nonnull
  static Scheduler scheduler()
  {
    return c_scheduler;
  }

  static void shutdown()
  {
    c_scheduler.shutdown();
    c_scheduler = new SchedulerImpl();
  }

  private static final class SchedulerImpl
    extends AbstractScheduler
  {
    @GwtIncompatible
    private final ScheduledExecutorService _executorService = new ScheduledThreadPoolExecutor( 1 );

    /**
     * {@inheritDoc}
     */
    @Override
    @GwtIncompatible
    public void shutdown()
    {
      _executorService.shutdown();
    }

    @Nonnull
    @Override
    @GwtIncompatible
    public SchedulerTask schedule( @Nonnull final Runnable task, final int delay )
    {
      final ScheduledFuture<?> future = _executorService.schedule( task, delay, TimeUnit.MILLISECONDS );
      return () -> future.cancel( true );

    }

    @Nonnull
    @Override
    @GwtIncompatible
    public SchedulerTask scheduleAtFixedRate( @Nonnull final Runnable task, final int period )
    {
      final ScheduledFuture<?> future = _executorService.scheduleAtFixedRate( task, 0, period, TimeUnit.MILLISECONDS );
      return () -> future.cancel( true );
    }
  }

  private static abstract class AbstractScheduler
    implements Scheduler
  {
    private final long _schedulerStart = System.currentTimeMillis();

    final long getSchedulerStart()
    {
      return _schedulerStart;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int now()
    {
      return (int) ( System.currentTimeMillis() - getSchedulerStart() );
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public SchedulerTask schedule( @Nonnull final Runnable task, final int delay )
    {
      final double timeoutId = DomGlobal.setTimeout( v -> task.run(), delay );
      return () -> DomGlobal.clearTimeout( timeoutId );
    }

    @Nonnull
    @Override
    public SchedulerTask scheduleAtFixedRate( @Nonnull final Runnable task, final int period )
    {
      final double timeoutId = DomGlobal.setInterval( v -> task.run(), period );
      return () -> DomGlobal.clearInterval( timeoutId );
    }
  }
}
