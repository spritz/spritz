package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import spritz.internal.util.CircularBuffer;
import static org.realityforge.braincheck.Guards.*;

/**
 * Base executor which other executors can extend.
 */
abstract class AbstractExecutor
  implements VirtualProcessorUnit.Executor
{
  /**
   * A queue containing tasks that have been scheduled but are not yet executing.
   */
  @Nonnull
  private final CircularBuffer<Runnable> _taskQueue;
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

  public void queue( @Nonnull final Runnable task )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> !_taskQueue.contains( task ),
                 () -> "Spritz-0098: Attempting to queue task " + task + " when task is already queued." );
    }
    _taskQueue.add( Objects.requireNonNull( task ) );
  }

  @Nonnull
  final CircularBuffer<Runnable> getTaskQueue()
  {
    return _taskQueue;
  }

  final void executeNextTask()
  {
    final Runnable task = _taskQueue.pop();
    assert null != task;
    try
    {
      task.run();
    }
    catch ( final Throwable t )
    {
      // Should we handle it with a per-task handler or a global error handler?
    }
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
   * Mark executor as ready for activation.
   * This typically means scheduling Executor to call activate on the correct VPU.
   */
  protected abstract void scheduleForActivation();
}
