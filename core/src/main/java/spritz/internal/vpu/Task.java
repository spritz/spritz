package spritz.internal.vpu;

import java.util.Objects;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.*;

/**
 * A task represents an executable element that can be run by the task executor.
 */
public final class Task
{
  /**
   * State when the task has not been scheduled.
   */
  private static final int STATE_IDLE = 0;
  /**
   * State when the task has been scheduled and should not be re-scheduled until next executed.
   */
  private static final int STATE_QUEUED = 1;
  /**
   * The callback to invoke when task is executed.
   */
  @Nonnull
  private final Runnable _work;
  /**
   * State of the task.
   */
  private int _state;

  public Task( @Nonnull final Runnable work )
  {
    _work = Objects.requireNonNull( work );
    _state = STATE_IDLE;
  }

  /**
   * Execute the work associated with the task.
   */
  void executeTask()
  {
    // It is possible that the task was executed outside the executor and
    // may no longer need to be executed. This particularly true when executing tasks
    // using the "idle until urgent" strategy.
    if ( isQueued() )
    {
      markAsIdle();
      runTask();
    }
  }

  /**
   * Actually execute the task, capture errors and send spy events.
   */
  private void runTask()
  {
    try
    {
      _work.run();
    }
    catch ( final Throwable t )
    {
      // Should we handle it with a per-task handler or a global error handler?
    }
  }

  /**
   * Mark task as being queued, first verifying that it is not already queued.
   * This is used so that task will not be able to be queued again until it has run.
   */
  void markAsQueued()
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( this::isIdle,
                 () -> "Spritz-0128: Attempting to queue task " + this + " when task is not idle." );
    }
    _state = STATE_QUEUED;
  }

  /**
   * Clear the queued flag, first verifying that the task is queued.
   */
  void markAsIdle()
  {
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( this::isQueued,
                 () -> "Spritz-0129: Attempting to clear queued flag on task " + this + " but task is not queued." );
    }
    _state = STATE_IDLE;
  }

  /**
   * Return true if task is idle or not disposed and not scheduled.
   *
   * @return true if task is idle.
   */
  private boolean isIdle()
  {
    return STATE_IDLE == _state;
  }

  /**
   * Return true if task is already scheduled.
   *
   * @return true if task is already scheduled.
   */
  private boolean isQueued()
  {
    return STATE_QUEUED == _state;
  }
}
