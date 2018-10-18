package streak.schedulers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Disposable;
import streak.Streak;

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
   * Schedules the execution of the given task.
   *
   * @param name the name of the task. Must be null unless {@link Streak#areNamesEnabled()} returns <code>true</code>.
   * @param task the task to execute.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Disposable schedule( @Nullable final String name, @Nonnull final Runnable task )
  {
    return schedule( name, task, 0 );
  }

  /**
   * Schedules the execution of the given task after a specified delay.
   *
   * @param name          the name of the task. Must be null unless {@link Streak#areNamesEnabled()} returns <code>true</code>.
   * @param task          the task to execute.
   * @param delayInMillis the delay before the task should execute measured in milliseconds.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Disposable schedule( @Nullable final String name, @Nonnull final Runnable task, final int delayInMillis )
  {
    return scheduleAtFixedRate( name, task, delayInMillis, 0 );
  }

  /**
   * Schedules the periodic execution of the given task with specified period, after a specified delay.
   *
   * @param name           the name of the task. Must be null unless {@link Streak#areNamesEnabled()} returns <code>true</code>.
   * @param task           the task to execute.
   * @param periodInMillis the period after execution when the task should be re-executed. A negative value is invalid while a value of 0 ndicates tasks is never rescheduled.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  default Disposable scheduleAtFixedRate( @Nullable final String name,
                                          @Nonnull final Runnable task,
                                          final int periodInMillis )
  {
    return scheduleAtFixedRate( name, task, 0, periodInMillis );
  }

  /**
   * Schedules the periodic execution of the given task with specified period, after a specified delay.
   *
   * @param name                 the name of the task. Must be null unless {@link Streak#areNamesEnabled()} returns <code>true</code>.
   * @param task                 the task to execute.
   * @param initialDelayInMillis the initial delay before the task should execute measured in milliseconds.
   * @param periodInMillis       the period after execution when the task should be re-executed. A negative value is invalid while a value of 0 ndicates tasks is never rescheduled.
   * @return the {@link Disposable} instance that can be used to cancel execution of task.
   */
  @Nonnull
  Disposable scheduleAtFixedRate( @Nullable String name,
                                  @Nonnull Runnable task,
                                  int initialDelayInMillis,
                                  int periodInMillis );
}
