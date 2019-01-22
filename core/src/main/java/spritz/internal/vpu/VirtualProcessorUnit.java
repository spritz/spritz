package spritz.internal.vpu;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Processing unit responsible for executing tasks.
 */
public abstract class VirtualProcessorUnit
{
  /**
   * The tasks ready to be executed by the VPU.
   */
  @Nonnull
  private final TaskQueue _taskQueue;

  /**
   * Create the processor unit.
   *
   * @param taskQueue the associated task queue.
   */
  protected VirtualProcessorUnit( @Nonnull final TaskQueue taskQueue )
  {
    _taskQueue = Objects.requireNonNull( taskQueue );
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
   * Return the associated task queue.
   *
   * @return the associated task queue.
   */
  @Nonnull
  protected final TaskQueue getTaskQueue()
  {
    return _taskQueue;
  }
}
