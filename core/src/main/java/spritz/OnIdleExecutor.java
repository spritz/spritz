package spritz;

import elemental2.dom.DomGlobal;

/**
 * Run tasks in next Idle callbacks.
 */
final class OnIdleExecutor
  extends DeadlineBasedExecutor
{
  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleForActivation()
  {
    DomGlobal.requestIdleCallback( deadline -> {

      context().activate( () -> executeTasks( () -> (int) deadline.timeRemaining() ) );
      //TODO: Omit return after Compiler externs updated and Elemental2 rebuilt
      return null;
    } );
  }
}
