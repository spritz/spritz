package streak.schedulers.m1;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import streak.Streak;
import static org.realityforge.braincheck.Guards.*;

/**
 * This scheduler executes tasks in rounds.
 * At the start of the round, the number of tasks currently queued is recorded and the scheduler executes that
 * number of tasks. At the end of the round there may be tasks remaining as execution of a task may result in
 * tasks being scheduled. Tasks have a priority and higher priority tasks will execute first, while tasks with the
 * same priority will execute in FIFO order.
 */
final class RoundBasedTaskExecutor
{
  /**
   * The default value for maximum number of rounds.
   */
  private static final int DEFAULT_MAX_ROUNDS = 100;
  /**
   * A task queue.
   */
  @Nonnull
  private final TaskQueue _taskQueue;
  /**
   * The maximum number of iterations that can be triggered in sequence without triggering an error. Set this
   * to 0 to disable check, otherwise trigger
   */
  private final int _maxRounds;
  /**
   * The current round.
   */
  private int _currentRound;
  /**
   * The number of tasks left in the current round.
   */
  private int _remainingTasksInCurrentRound;

  RoundBasedTaskExecutor( @Nonnull final TaskQueue taskQueue )
  {
    this( taskQueue, DEFAULT_MAX_ROUNDS );
  }

  RoundBasedTaskExecutor( @Nonnull final TaskQueue taskQueue, final int maxRounds )
  {
    assert maxRounds > 0;
    _taskQueue = Objects.requireNonNull( taskQueue );
    _maxRounds = maxRounds;
  }

  /**
   * Return the maximum number of rounds before runaway task is detected.
   *
   * @return the maximum number of rounds.
   */
  int getMaxRounds()
  {
    return _maxRounds;
  }

  /**
   * Return true if tasks are currently executing, false otherwise.
   *
   * @return true if tasks are currently executing, false otherwise.
   */
  boolean areTasksExecuting()
  {
    return 0 != _currentRound;
  }

  /**
   * Add the specified task to the list of pending tasks.
   * The task must not already be in the list of pending tasks.
   *
   * @param task the task.
   */
  void scheduleTask( @Nonnull final Task task )
  {
    if ( Streak.shouldCheckInvariants() )
    {
      invariant( () -> !task.isScheduled(),
                 () -> "Streak-0095: Attempting to schedule task named '" + task.getName() +
                       "' when task is already scheduled." );
    }
    task.markAsScheduled();
    _taskQueue.queueTask( task );
  }

  /**
   * If the scheduler is not already executing pending tasks then run pending tasks until
   * complete or runaway tasks detected.
   */
  void runPendingTasks()
  {
    while ( true )
    {
      if ( !runTask() )
      {
        break;
      }
    }
  }

  /**
   * Execute the next pending task if any.
   * <ul>
   * <li>If there is any reactions left in this round then run the next reaction and consume a token.</li>
   * <li> If there are more rounds left in budget and more pending tasks then start a new round,
   * allocating a number of tokens equal to the number of pending tasks, run the next task
   * and consume a token.</li>
   * <li>Otherwise runaway tasks are detected, so act appropriately.</li>
   * </ul>
   *
   * @return true if a task was ran, false otherwise.
   */
  boolean runTask()
  {
    // If we have reached the last task in this round then
    // determine if we need any more rounds and if we do ensure
    if ( 0 == _remainingTasksInCurrentRound )
    {
      final int pendingTasksCount = _taskQueue.getQueueSize();
      if ( 0 == pendingTasksCount )
      {
        _currentRound = 0;
        return false;
      }
      else if ( _currentRound + 1 > _maxRounds )
      {
        _currentRound = 0;
        onRunawayTasksDetected();
        return false;
      }
      else
      {
        _currentRound = _currentRound + 1;
        _remainingTasksInCurrentRound = pendingTasksCount;
      }
    }
    /*
     * If we get to here there are still tasks that need processing and we have not
     * exceeded our round budget. So we pop a task off the list and process it.
     *
     * The first task is chosen as the same task will only be executed multiple times
     * per round if there is no higher priority tasks and there is some lower priority
     * tasks. This means that when runaway task detection code is active, the list of
     * pending tasks starts with those tasks that have likely lead to the runaway condition.
     */
    _remainingTasksInCurrentRound--;

    final Task task = _taskQueue.dequeueTask();
    assert null != task;
    executeTask( task );
    return true;
  }

  protected void executeTask( @Nonnull final Task task )
  {
    // It is possible that the task was executed outside the executor and
    // may no longer need to be executed. This particularly true when executing tasks
    // using the "idle until urgent" strategy.
    if ( task.isScheduled() )
    {
      task.markAsExecuted();
      try
      {
        task.getTask().run();
      }
      catch ( final Throwable t )
      {
        //TODO: Send error to per-task or global error handler?
      }
    }
  }

  /**
   * Called when runaway tasks detected.
   * Depending on configuration will optionally purge the pending
   * tasks and optionally fail an invariant check.
   */
  void onRunawayTasksDetected()
  {
    final List<String> taskNames =
      Streak.shouldCheckInvariants() && BrainCheckConfig.verboseErrorMessages() ?
      _taskQueue.getOrderedTasks()
        .map( Task::getName )
        .collect( Collectors.toList() ) :
      null;

    if ( Streak.purgeTasksWhenRunawayDetected() )
    {
      final Collection<Task> tasks = _taskQueue.clear();
      for ( final Task task : tasks )
      {
        task.markAsExecuted();
      }
    }

    if ( Streak.shouldCheckInvariants() )
    {
      fail( () -> "Streak-0101: Runaway task(s) detected. Tasks still running after " + _maxRounds +
                  " rounds. Current tasks include: " + taskNames );
    }
  }
}
