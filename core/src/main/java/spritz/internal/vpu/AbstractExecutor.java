package spritz.internal.vpu;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import spritz.Task;
import spritz.VirtualProcessorUnit;
import spritz.internal.util.CircularBuffer;
import static org.realityforge.braincheck.Guards.*;

/**
 * Base executor which other executors can extend.
 */
public abstract class AbstractExecutor
  implements VirtualProcessorUnit.Executor
{
  /**
   * A queue containing tasks that have been scheduled but are not yet executing.
   */
  @Nonnull
  private final CircularBuffer<Task> _taskQueue;
  @Nullable
  private VirtualProcessorUnit.Context _context;

  AbstractExecutor()
  {
    _taskQueue = new CircularBuffer<>( 100 );
  }

  final int getQueueSize()
  {
    return _taskQueue.size();
  }

  public void queue( @Nonnull final Task task )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> !_taskQueue.contains( task ),
                 () -> "Spritz-0098: Attempting to queue task " + task + " when task is already queued." );
    }
    Objects.requireNonNull( task );
    task.markAsQueued();
    _taskQueue.add( task );
  }

  @Nonnull
  final CircularBuffer<Task> getTaskQueue()
  {
    return _taskQueue;
  }

  final void executeNextTask()
  {
    final Task task = _taskQueue.pop();
    assert null != task;
    task.executeTask();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void init( @Nonnull final VirtualProcessorUnit.Context context )
  {
    _context = context;
  }

  @Nonnull
  final VirtualProcessorUnit.Context context()
  {
    assert null != _context;
    return _context;
  }

  /**
   * Perform the activation and execute tasks as required.
   */
  protected abstract void activate();

  /**
   * Mark executor as ready for activation.
   * This typically means scheduling Executor to call activate on the correct VPU.
   */
  protected abstract void scheduleForActivation();
}
