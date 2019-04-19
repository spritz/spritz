package spritz;

import elemental2.dom.DomGlobal;

/**
 * Run tasks in next AnimationFrame.
 */
final class AnimationFrameExecutor
  extends RoundBasedExecutor
{
  @Override
  final void scheduleForActivation()
  {
    DomGlobal.requestAnimationFrame( v -> activate() );
  }
}
