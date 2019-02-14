package spritz;

import elemental2.dom.DomGlobal;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import spritz.internal.annotations.GwtIncompatible;

final class SchedulerHolder
{
  @Nonnull
  private static AbstractScheduler c_scheduler = new SchedulerImpl();

  private SchedulerHolder()
  {
  }

  @Nonnull
  static Scheduler scheduler()
  {
    return c_scheduler;
  }

  static void reset()
  {
    c_scheduler.shutdown();
    c_scheduler = new SchedulerImpl();
  }

  private static final class SchedulerImpl
    extends AbstractScheduler
  {
    @GwtIncompatible
    private final ScheduledExecutorService _executorService = new ScheduledThreadPoolExecutor( 1 );

    @GwtIncompatible
    void shutdown()
    {
      _executorService.shutdown();
    }

    @GwtIncompatible
    @Nonnull
    @Override
    protected Cancelable doSchedule( @Nonnull final Runnable task, final int delay )
    {
      final ScheduledFuture<?> future = _executorService.schedule( task, delay, TimeUnit.MILLISECONDS );
      return () -> future.cancel( true );
    }

    @Nonnull
    @Override
    @GwtIncompatible
    protected Cancelable doScheduleAtFixedRate( @Nonnull final Runnable task, final int period )
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

    void shutdown()
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
    public final Cancelable schedule( @Nonnull final Runnable task, final int delay )
    {
      return doSchedule( task, delay );
    }

    @Nonnull
    protected Cancelable doSchedule( @Nonnull final Runnable task, final int delay )
    {
      final double timeoutId = DomGlobal.setTimeout( v -> task.run(), delay );
      return () -> DomGlobal.clearTimeout( timeoutId );
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final Cancelable scheduleAtFixedRate( @Nonnull final Runnable task, final int period )
    {
      return doScheduleAtFixedRate( task, period );
    }

    @Nonnull
    protected Cancelable doScheduleAtFixedRate( @Nonnull final Runnable task, final int period )
    {
      final double timeoutId = DomGlobal.setInterval( v -> task.run(), period );
      return () -> DomGlobal.clearInterval( timeoutId );
    }
  }
}
