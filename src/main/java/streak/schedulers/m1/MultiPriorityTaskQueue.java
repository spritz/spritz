package streak.schedulers.m1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Streak;
import static org.realityforge.braincheck.Guards.*;

/**
 * Basic implementation of task queue that supports priority based queuing of tasks.
 */
final class MultiPriorityTaskQueue
  implements TaskQueue
{
  /**
   * The default number of priorities.
   */
  private static final int DEFAULT_PRIORITY_COUNT = 5;
  /**
   * The default of slots in each priority buffer.
   */
  private static final int DEFAULT_BUFFER_SIZE = 100;
  /**
   * A buffer per priority containing tasks that have been scheduled but are not executing.
   */
  @Nonnull
  private final CircularBuffer<Task>[] _taskQueues;
  @Nonnull
  private final Function<Task, Integer> _priorityMapper;

  /**
   * Construct queue with priority count specified by {@link #DEFAULT_PRIORITY_COUNT} where each priority is backed by a buffer with default size specified by {@link #DEFAULT_BUFFER_SIZE}.
   *
   * @param priorityMapper the function that maps task to priority.
   */
  MultiPriorityTaskQueue( @Nonnull final Function<Task, Integer> priorityMapper )
  {
    this( DEFAULT_PRIORITY_COUNT, priorityMapper, DEFAULT_BUFFER_SIZE );
  }

  /**
   * Construct queue with specified priority count where each priority is backed by a buffer with default size specified by {@link #DEFAULT_BUFFER_SIZE}.
   *
   * @param priorityCount  the number of priorities supported.
   * @param priorityMapper the function that maps task to priority.
   */
  MultiPriorityTaskQueue( final int priorityCount, @Nonnull final Function<Task, Integer> priorityMapper )
  {
    this( priorityCount, priorityMapper, DEFAULT_BUFFER_SIZE );
  }

  /**
   * Construct queue with specified priority count where each priority is backed by a buffer with specified size.
   *
   * @param priorityCount  the number of priorities supported.
   * @param priorityMapper the function that maps task to priority.
   * @param bufferSize     the initial size of buffer for each priority.
   */
  @SuppressWarnings( "unchecked" )
  MultiPriorityTaskQueue( final int priorityCount,
                          @Nonnull final Function<Task, Integer> priorityMapper,
                          final int bufferSize )
  {
    assert priorityCount > 0;
    assert bufferSize > 0;
    _taskQueues = (CircularBuffer<Task>[]) new CircularBuffer[ priorityCount ];
    for ( int i = 0; i < priorityCount; i++ )
    {
      _taskQueues[ i ] = new CircularBuffer<>( bufferSize );
    }
    _priorityMapper = Objects.requireNonNull( priorityMapper );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getQueueSize()
  {
    int count = 0;
    for ( final CircularBuffer<Task> taskQueue : _taskQueues )
    {
      count += taskQueue.size();
    }
    return count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasTasks()
  {
    for ( final CircularBuffer<Task> taskQueue : _taskQueues )
    {
      if ( !taskQueue.isEmpty() )
      {
        return true;
      }
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void queueTask( @Nonnull final Task task )
  {
    if ( Streak.shouldCheckInvariants() )
    {
      invariant( () -> Arrays.stream( _taskQueues ).noneMatch( b -> b.contains( task ) ),
                 () -> "Streak-0099: Attempting to schedule task named '" + task.getName() +
                       "' when task is already in queues." );
    }
    _taskQueues[ _priorityMapper.apply( task ) ].add( Objects.requireNonNull( task ) );
  }

  /**
   * {@inheritDoc}
   */
  @Nullable
  @Override
  public Task dequeueTask()
  {
    // Return the highest priority taskQueue that has tasks in it and return task.
    for ( final CircularBuffer<Task> taskQueue : _taskQueues )
    {
      if ( !taskQueue.isEmpty() )
      {
        final Task task = taskQueue.pop();
        assert null != task;
        return task;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Task> clear()
  {
    final ArrayList<Task> tasks = new ArrayList<>();
    for ( final CircularBuffer<Task> taskQueue : _taskQueues )
    {
      taskQueue.stream().forEach( tasks::add );
      taskQueue.clear();
    }
    return tasks;
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public Stream<Task> getOrderedTasks()
  {
    assert Streak.shouldCheckInvariants() || Streak.shouldCheckApiInvariants();
    return Stream.of( _taskQueues ).flatMap( CircularBuffer::stream );
  }
}
