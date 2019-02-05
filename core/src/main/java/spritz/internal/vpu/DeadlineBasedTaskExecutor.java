package spritz.internal.vpu;

import javax.annotation.Nullable;

/**
 * This executor runs tasks until a deadline has been reached.
 */
final class DeadlineBasedTaskExecutor
  extends AbstractExecutor
{
  @FunctionalInterface
  public interface DeadlineFunction
  {
    int getTimeRemaining();
  }

  /**
   * Returns true if the executor should yield and return control to invoker.
   *
   * @param function    the function that specifies deadline, if any.
   * @param minTaskTime the minimum task time in milliseconds.
   * @return true to yield to caller, false to continue executing tasks.
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
    int queueSize;
    while ( 0 != ( queueSize = getTaskQueueSize() ) && !shouldYield( function, 1 ) )
    {
      executeNextTask();
    }
    if ( 0 != queueSize )
    {
      //TODO: Reschedule?
    }
  }
}
