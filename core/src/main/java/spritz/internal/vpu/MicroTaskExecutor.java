package spritz.internal.vpu;

import elemental2.promise.Promise;

/**
 * Run tasks in next MicroTask.
 */
public final class MicroTaskExecutor
  extends RoundBasedExecutor
{
  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleForActivation()
  {
    new Promise<>( null ).then( v -> {
      context().activate( this::executeTasks );
      return null;
    } );
  }
}
