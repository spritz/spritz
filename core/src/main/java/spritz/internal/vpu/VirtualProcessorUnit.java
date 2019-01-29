package spritz.internal.vpu;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Processing unit responsible for executing tasks.
 */
public final class VirtualProcessorUnit
{
  /**
   * The executor responsible for selecting and invoking tasks.
   * This executor is expected to share a reference to the {@link TaskQueue}.
   */
  @Nonnull
  private final TaskExecutor _executor;

  /**
   * Create the processor unit.
   *
   * @param executor the associated task executor.
   */
  public VirtualProcessorUnit( @Nonnull final TaskExecutor executor )
  {
    _executor = Objects.requireNonNull( executor );
    _executor.init( this::activate );
  }

  /**
   * Return the VirtualProcessorUnit that is currently executing.
   * This method MUST NOT be invoked if there is no {@link VirtualProcessorUnit} activated.
   *
   * @return the VirtualProcessorUnit that is currently executing.
   */
  @Nonnull
  public static VirtualProcessorUnit current()
  {
    return VirtualProcessorUnitHolder.current();
  }

  /**
   * Queue the specified task for execution and enable the VirtualProcessorUnit for activation if necessary.
   * The task must not be already queued.
   *
   * @param task the task.
   */
  public void queueTask( @Nonnull final Task task )
  {
    _executor.queueTask( task );
  }

  @Nonnull
  public Task createTask( @Nonnull final Task.Action work, final int flags )
  {
    return new Task( work, flags );
  }

  /**
   * Return the associated task executor.
   *
   * @return the associated task executor.
   */
  @Nonnull
  protected final TaskExecutor getExecutor()
  {
    return _executor;
  }

  /**
   * Activate the unit.
   * This involves setting current unit, invoking the activation function and clearing the current unit.
   * It is an error to invoke this method if there is already a current unit.
   *
   * @param activationFn the activation function.
   * @see ExecutorContext#activate(ExecutorContext.ActivationFn)
   */
  private void activate( @Nonnull final ExecutorContext.ActivationFn activationFn )
  {
    VirtualProcessorUnitHolder.activate( this );
    try
    {
      activationFn.invoke();
    }
    finally
    {
      VirtualProcessorUnitHolder.deactivate( this );
    }
  }
}
