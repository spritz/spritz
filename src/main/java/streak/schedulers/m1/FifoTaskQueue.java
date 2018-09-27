package streak.schedulers.m1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Streak;
import static org.realityforge.braincheck.Guards.*;

/**
 * A very simple first-in first out task queue.
 */
final class FifoTaskQueue
  implements TaskQueue
{
  /**
   * The default number of slots in queue.
   */
  private static final int DEFAULT_QUEUE_SIZE = 100;
  /**
   * A buffer per priority containing tasks that have been scheduled but are not executing.
   */
  @Nonnull
  private final CircularBuffer<Task> _taskQueue;

  FifoTaskQueue()
  {
    this( DEFAULT_QUEUE_SIZE );
  }

  FifoTaskQueue( final int queueSize )
  {
    _taskQueue = new CircularBuffer<>( queueSize );
  }

  @Override
  public int getQueueSize()
  {
    return _taskQueue.size();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasTasks()
  {
    return !_taskQueue.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void queueTask( @Nonnull final Task task )
  {
    if ( Streak.shouldCheckInvariants() )
    {
      invariant( () -> !_taskQueue.contains( task ),
                 () -> "Streak-0089: Attempting to schedule task named '" + task.getName() +
                       "' when task is already in queue." );
    }
    _taskQueue.add( Objects.requireNonNull( task ) );
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public Task dequeueTask()
  {
    return _taskQueue.pop();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Task> clear()
  {
    final ArrayList<Task> tasks = new ArrayList<>();
    _taskQueue.stream().forEach( tasks::add );
    _taskQueue.clear();
    return tasks;
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public Stream<Task> getOrderedTasks()
  {
    return _taskQueue.stream();
  }
}
