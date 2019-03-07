package spritz;

/**
 * Run tasks in next MacroTask.
 */
final class MacroTaskExecutor
  extends RoundBasedExecutor
{
  /**
   * {@inheritDoc}
   */
  @Override
  final void scheduleForActivation()
  {
    Scheduler.schedule( this::activate, 0 );
  }
}
