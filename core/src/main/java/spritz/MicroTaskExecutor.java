package spritz;

import elemental2.promise.Promise;

/**
 * Run tasks in next MicroTask.
 */
final class MicroTaskExecutor
  extends RoundBasedExecutor
{
  @Override
  final void scheduleForActivation()
  {
    new Promise<>( null ).then( v -> {
      activate();
      return null;
    } );
  }
}
