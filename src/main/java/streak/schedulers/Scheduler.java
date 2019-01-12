package streak.schedulers;

import javax.annotation.Nonnull;

/**
 * The scheduler is responsible for scheduling and executing tasks asynchronously.
 * The scheduler provides an "abstract asynchronous boundary" to stream operators.
 *
 * <p>The scheduler has an internal clock that represents time as a monotonically increasing
 * <code>int</code> value. The value may or may not have a direct relationship to wall-clock
 * time and the unit of the value is defined by the implementation..</p>
 */
public interface Scheduler
{
  /**
   * Return a value representing the "current time" of the scheduler.
   *
   * @return the "current time" of the scheduler.
   */
  int now();

  /**
   * Schedules the execution of the given task as soon as possible but still asynchronously.
   *
   * @param task the task to execute.
   * @return the {@link Task} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Task schedule( @Nonnull final Runnable task )
  {
    return schedule( task, 0 );
  }

  /**
   * Schedules the execution of the given task after a specified delay.
   *
   * @param task  the task to execute.
   * @param delay the delay before the task should execute.
   * @return the {@link Task} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Task schedule( @Nonnull final Runnable task, final int delay )
  {
    return schedule( task, delay, 0 );
  }

  /**
   * Schedules the periodic execution of the given task with specified period.
   *
   * @param task   the task to execute.
   * @param period the period after execution when the task should be re-executed. A negative value is invalid while a value of 0 indicates that the task is never rescheduled.
   * @return the {@link Task} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Task scheduleAtFixedRate( @Nonnull final Runnable task,
                                    final int period )
  {
    return schedule( task, 0, period );
  }

  /**
   * Schedules the periodic execution of the given task with specified period, after a specified delay.
   *
   * @param task         the task to execute.
   * @param initialDelay the initial delay before the task should execute.
   * @param period       the period after execution when the task should be re-executed. A negative value is invalid while a value of 0 indicates that the task is never rescheduled.
   * @return the {@link Task} instance that can be used to cancel execution of task.
   */
  @Nonnull
  Task schedule( @Nonnull Runnable task, int initialDelay, int period );

  /**
   * Initiate an orderly shutdown of the scheduler.
   * No new tasks will be accepted but previously submitted tasks may be completed
   * depending on the policy of the underlying scheduler.
   *
   * <p>Note: It is expected that this will be eliminated from the public interface exposed to user code
   * and will only be visible internally to the framework.</p>
   */
  void shutdown();
}
