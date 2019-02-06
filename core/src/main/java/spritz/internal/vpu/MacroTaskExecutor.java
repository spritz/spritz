package spritz.internal.vpu;

import spritz.Spritz;

/**
 * Run tasks in next MacroTask.
 */
public final class MacroTaskExecutor
  extends RoundBasedExecutor
{
  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleForActivation()
  {
    Spritz.scheduler().schedule( () -> context().activate( this::executeTasks ), 0 );
  }
}
