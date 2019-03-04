package spritz;

import javax.annotation.Nullable;

/**
 * This executor runs tasks until a deadline has been reached.
 */
abstract class DeadlineBasedExecutor
  extends AbstractExecutor
{
  private static final int MIN_TASK_TIME = 1;

  @FunctionalInterface
  interface DeadlineFunction
  {
    int getTimeRemaining();
  }

  /**
   * Returns true if the executor should yield and return control to invoker.
   *
   * @param function the function that specifies deadline, if any.
   * @return true to yield to caller, false to continue executing tasks.
   */
  private boolean shouldYield( @Nullable final DeadlineFunction function )
  {
    return null != function && function.getTimeRemaining() <= MIN_TASK_TIME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void activate()
  {
    context().activate( () -> executeTasks( null ) );
  }

  /**
   * Run tasks until deadline exceeded or all tasks completed.
   */
  void executeTasks( @Nullable final DeadlineFunction function )
  {
    int queueSize;
    while ( 0 != ( queueSize = getQueueSize() ) && !shouldYield( function ) )
    {
      executeNextTask();
    }
    if ( 0 != queueSize )
    {
      scheduleForActivation();
    }
  }
}
