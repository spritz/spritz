package spritz;

import static org.realityforge.braincheck.Guards.*;

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
    if ( VirtualProcessorUnitsHolder.isVirtualProcessorUnitActivated() )
    {
      if ( Spritz.shouldCheckInvariants() )
      {
        invariant( () -> VirtualProcessorUnitsHolder.current() == VirtualProcessorUnitsHolder.direct(),
                   () -> "Spritz-0030: DirectExecutor.scheduleForActivation() called in context of unexpected VPU " +
                         VirtualProcessorUnitsHolder.current().getName() );
      }
    }
    else
    {
      activate();
    }
  }
}
