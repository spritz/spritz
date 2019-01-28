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
   * Activate the processor.
   * This method MUST only be called if there is no current processor unit activated.
   * The activation will set the {@link #current()} VirtualProcessorUnit for the duration
   * of the activation and invoke an
   */
  protected void activate()
  {
    VirtualProcessorUnitHolder.activate( this );
    try
    {
      _executor.executeTasks();
    }
    finally
    {
      VirtualProcessorUnitHolder.deactivate( this );
    }
  }
}
