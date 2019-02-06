package spritz;

import javax.annotation.Nullable;

/**
 * This executor runs tasks until a deadline has been reached.
 */
abstract class DeadlineBasedExecutor
  extends AbstractExecutor
{
  @FunctionalInterface
  interface DeadlineFunction
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
    while ( 0 != ( queueSize = getQueueSize() ) && !shouldYield( function, 1 ) )
    {
      executeNextTask();
    }
    if ( 0 != queueSize )
    {
      //TODO: Or maybe repeat by default and just skip cancelling?
      scheduleForActivation();
    }
  }
}