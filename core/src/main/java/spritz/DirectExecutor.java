package spritz;

/**
 * Run tasks in the current execution context.
 * Any task queued will run immediately.
 */
final class DirectExecutor
  extends RoundBasedExecutor
{
  DirectExecutor()
  {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleForActivation()
  {
    executeTasks();
  }
}
