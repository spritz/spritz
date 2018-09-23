package streak;

import javax.annotation.Nonnull;

/**
 * The scheduler is responsible for scheduling and executing tasks asynchronously.
 * The scheduler provides an "abstract asynchronous boundary" to stream operators.
 */
public interface Scheduler
{
  /**
   * Returns the "current time" of the scheduler.
   * This time is an abstract time measure that is only guaranteed to be a monotonically increasing value.
   *
   * @return the "current time" of the scheduler.
   */
  int now();

  /**
   * Schedules the execution of the given task.
   *
   * @param task the task to execute.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Disposable schedule( @Nonnull Runnable task )
  {
    return schedule( task, 0 );
  }

  /**
   * Schedules the execution of the given task after a specified delay.
   *
   * @param task          the task to execute.
   * @param delayInMillis the delay before the task should execute measured in milliseconds.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  Disposable schedule( @Nonnull Runnable task, int delayInMillis );

  /**
   * Schedules the periodic execution of the given task with specified period, after a specified delay.
   *
   * @param task           the task to execute.
   * @param periodInMillis the period after execution when the task should be re-executed.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Disposable scheduleAtFixedRate( @Nonnull Runnable task, int periodInMillis )
  {
    return scheduleAtFixedRate( task, 0, periodInMillis );
  }

  /**
   * Schedules the periodic execution of the given task with specified period, after a specified delay.
   *
   * @param task                 the task to execute.
   * @param initialDelayInMillis the initial delay before the task should execute measured in milliseconds.
   * @param periodInMillis       the period after execution when the task should be re-executed.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  Disposable scheduleAtFixedRate( @Nonnull Runnable task, int initialDelayInMillis, int periodInMillis );
}
