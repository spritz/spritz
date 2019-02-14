package spritz;

import javax.annotation.Nonnull;

/**
 * The scheduler is responsible for scheduling and executing tasks asynchronously.
 * The scheduler provides an "abstract asynchronous boundary" to stream operators.
 *
 * <p>The scheduler has an internal clock that represents time as a monotonically increasing
 * <code>int</code> value. The value may or may not have a direct relationship to wall-clock
 * time and the unit of the value is defined by the implementation.</p>
 */
public interface Scheduler
{
  /**
   * Return the spritz scheduler.
   *
   * @return the spritz scheduler.
   */
  @Nonnull
  static Scheduler instance()
  {
    return SchedulerHolder.scheduler();
  }

  /**
   * Return a value representing the "current time" of the scheduler.
   *
   * @return the "current time" of the scheduler.
   */
  int now();

  /**
   * Schedules the execution of the given task after a specified delay.
   *
   * @param task  the task to execute.
   * @param delay the delay before the task should execute.
   * @return the {@link Cancelable} instance that can be used to cancel execution of the task.
   */
  @Nonnull
  Cancelable schedule( @Nonnull final Runnable task, final int delay );

  /**
   * Schedules the periodic execution of the given task with specified period.
   *
   * @param task   the task to execute.
   * @param period the period after execution when the task should be re-executed. A negative value is invalid while a value of 0 indicates that the task is never rescheduled.
   * @return the {@link Cancelable} instance that can be used to cancel execution of the task.
   */
  @Nonnull
  Cancelable scheduleAtFixedRate( @Nonnull final Runnable task, final int period );
}
