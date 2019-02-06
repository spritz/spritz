package spritz;

import elemental2.dom.DomGlobal;

/**
 * Run tasks in next AnimationFrame.
 */
final class AnimationFrameExecutor
  extends RoundBasedExecutor
{
  /**
   * {@inheritDoc}
   */
  @Override
  protected final void scheduleForActivation()
  {
    int[] frame = new int[ 1 ];
    frame[ 0 ] = DomGlobal.requestAnimationFrame( v -> {
      context().activate( this::executeTasks );
      DomGlobal.cancelAnimationFrame( frame[ 0 ] );
    } );
  }
}
