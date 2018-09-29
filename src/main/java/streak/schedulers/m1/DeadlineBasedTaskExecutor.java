package streak.schedulers.m1;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This executor runs tasks until a deadline has been reached.
 */
final class DeadlineBasedTaskExecutor
  extends AbstractTaskExecutor
{
  @FunctionalInterface
  public interface DeadlineFunction
  {
    int getTimeRemaining();
  }

  DeadlineBasedTaskExecutor( @Nonnull final TaskQueue taskQueue )
  {
    super( taskQueue );
  }

  /**
   * Returns true if the executor should yield and return control to invoker.
   *
   * @param function    the function that specifies deadline, if any.
   * @param minTaskTime the minimum task time in milliseconds.
   * @return true to yield to the caller, false to continue executing tasks.
   */
  private boolean shouldYield( @Nullable final DeadlineFunction function, final int minTaskTime )
  {
    return null != function && function.getTimeRemaining() <= minTaskTime;
  }

  /**
   * Run tasks until deadline exceeded or all tasks completed.
   */
  void runTasks( @Nullable final DeadlineFunction function )
  {
    while ( !shouldYield( function, 1 ) )
    {
      final Task task = getTaskQueue().dequeueTask();
      if ( null == task )
      {
        return;
      }
      else
      {
        executeTask( task );
      }
    }
    if ( getTaskQueue().hasTasks() )
    {
      //TODO: Reschedule?
    }
  }
}
