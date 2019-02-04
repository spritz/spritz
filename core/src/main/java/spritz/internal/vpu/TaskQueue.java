package spritz.internal.vpu;

import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import spritz.Task;
import spritz.internal.util.CircularBuffer;
import static org.realityforge.braincheck.Guards.*;

/**
 * A very simple first-in first out task queue.
 */
public final class TaskQueue
{
  /**
   * A buffer per priority containing tasks that have been scheduled but are not executing.
   */
  @Nonnull
  private final CircularBuffer<Task> _buffer;

  public TaskQueue( final int initialCapacity )
  {
    _buffer = new CircularBuffer<>( initialCapacity );
  }

  int getQueueSize()
  {
    return _buffer.size();
  }

  public boolean isEmpty()
  {
    return _buffer.isEmpty();
  }

  /**
   * Add the specified task to the queue.
   * The task must not already be in the queue.
   *
   * @param task the task.
   */
  public void queue( @Nonnull final Task task )
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> !_buffer.contains( task ),
                 () -> "Spritz-0098: Attempting to queue task " + task + " when task is already queued." );
    }
    Objects.requireNonNull( task );
    task.markAsQueued();
    _buffer.add( task );
  }

  @Nullable
  Task dequeue()
  {
    return _buffer.pop();
  }

  @Nonnull
  Stream<Task> stream()
  {
    return _buffer.stream();
  }
}
