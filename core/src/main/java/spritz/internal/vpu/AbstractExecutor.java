package spritz.internal.vpu;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import spritz.Task;
import spritz.internal.util.CircularBuffer;
import static org.realityforge.braincheck.Guards.*;

/**
 * Base executor which other executors can extend.
 */
public abstract class AbstractExecutor
{
  /**
   * A queue containing tasks that have been scheduled but are not yet executing.
   */
  @Nonnull
  private final CircularBuffer<Task> _taskQueue;

  protected AbstractExecutor()
  {
    _taskQueue = new CircularBuffer<>( 100 );
  }

  public final int getTaskQueueSize()
  {
    return _taskQueue.size();
  }

  public final void queue( @Nonnull final Task task )
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
  protected final CircularBuffer<Task> getTaskQueue()
  {
    return _taskQueue;
  }

  protected final void executeNextTask()
  {
    final Task task = _taskQueue.pop();
    assert null != task;
    task.executeTask();
  }
}
