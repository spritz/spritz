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
   * Interface for action executed by task.
   */
  @FunctionalInterface
  public interface Action
  {
    /**
     * Perform the action.
     */
    void call();
  }

  /**
   * The code to invoke when task is executed.
   */
  @Nonnull
  private final Action _work;
  /**
   * State of the task.
   */
  private int _flags;

  public Task( @Nonnull final Action work, final int flags )
  {
    _work = Objects.requireNonNull( work );
    _flags = flags | Flags.STATE_IDLE | Flags.priority( flags );
  }

  /**
   * Re-schedule this task if it is idle and trigger the scheduler if it is not active.
   */
  public void schedule()
  {
    if ( isIdle() )
    {
      queueTask();
    }
  }

  int getFlags()
  {
    return _flags;
  }

  void queueTask()
  {
    //getContext().getTaskQueue().queueTask( this );
  }

  /**
   * Return the priority of the task.
   * This is only meaningful when TaskQueue observes priority.
   *
   * @return the priority of the task.
   */
  int getPriorityIndex()
  {
    return Flags.getPriorityIndex( _flags );
  }

  /**
   * Return the task.
   *
   * @return the task.
   */
  @Nonnull
  Action getWork()
  {
    return _work;
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

      if ( 0 == ( _flags & Flags.NO_WRAP_TASK ) )
      {
        runTask();
      }
      else
      {
        // It is expected that the task catches errors and handles internally. Thus no need to catch errors here.
        _work.call();
      }

      // If this task has been marked as a task to dispose on completion then do so
      if ( 0 != ( _flags & Flags.DISPOSE_ON_COMPLETE ) )
      {
        dispose();
      }
    }
  }

  /**
   * Actually execute the task, capture errors and send spy events.
   */
  private void runTask()
  {
    try
    {
      getWork().call();
    }
    catch ( final Throwable t )
    {
      // Should we handle it with a per-task handler or a global error handler?
    }
  }

  public void dispose()
  {
    if ( isNotDisposed() )
    {
      _flags = Flags.setState( _flags, Flags.STATE_DISPOSED );
    }
  }

  public boolean isDisposed()
  {
    return Flags.STATE_DISPOSED == Flags.getState( _flags );
  }

  public boolean isNotDisposed()
  {
    return !isDisposed();
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
    _flags = Flags.setState( _flags, Flags.STATE_QUEUED );
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
    _flags = Flags.setState( _flags, Flags.STATE_IDLE );
  }

  /**
   * Return true if task is idle or not disposed and not scheduled.
   *
   * @return true if task is idle.
   */
  boolean isIdle()
  {
    return Flags.STATE_IDLE == Flags.getState( _flags );
  }

  /**
   * Return true if task is already scheduled.
   *
   * @return true if task is already scheduled.
   */
  boolean isQueued()
  {
    return Flags.STATE_QUEUED == Flags.getState( _flags );
  }

  public static final class Flags
  {
    /**
     * Highest priority.
     * This priority should be used when the task will dispose or release other reactive elements
     * (and thus remove elements from being scheduled). Only one of the PRIORITY_* flags should be applied to a task.
     */
    public static final int PRIORITY_HIGHEST = 0b001 << 15;
    /**
     * High priority.
     * To reduce the chance that downstream elements will react multiple times within a single
     * reaction round, this priority should be used when the task may trigger many
     * downstream tasks. Only one of the PRIORITY_* flags should be applied to a task.
     */
    public static final int PRIORITY_HIGH = 0b010 << 15;
    /**
     * Normal priority if no other priority otherwise specified. Only one of
     * the PRIORITY_* flags should be applied to a task.
     */
    public static final int PRIORITY_NORMAL = 0b011 << 15;
    /**
     * Low priority. Typically used to schedule tasks that reflect state onto non-reactive
     * application components.  Only one of the PRIORITY_* flags should be applied to a task.
     */
    public static final int PRIORITY_LOW = 0b100 << 15;
    /**
     * Lowest priority. Only one of the PRIORITY_* flags should be applied to a task.
     */
    public static final int PRIORITY_LOWEST = 0b101 << 15;
    /**
     * Mask used to extract priority bits.
     */
    static final int PRIORITY_MASK = 0b111 << 15;
    /**
     * Shift used to extract priority after applying mask.
     */
    private static final int PRIORITY_SHIFT = 15;
    /**
     * The number of priority levels.
     */
    static final int PRIORITY_COUNT = 5;
    /**
     * The flag that indicates that task should not be wrapped.
     * The wrapping is responsible for ensuring the task never generates an exception and for generating
     * the spy events. If wrapping is disabled it is expected that the caller is responsible for integrating
     * with the spy subsystem and catching exceptions if any.
     */
    public static final int NO_WRAP_TASK = 1 << 20;
    /**
     * The flag that specifies that the task should be disposed after it has completed execution.
     */
    public static final int DISPOSE_ON_COMPLETE = 1 << 19;
    /**
     * Mask containing flags that can be applied to a task.
     */
    static final int TASK_FLAGS_MASK =
      DISPOSE_ON_COMPLETE | NO_WRAP_TASK;
    /**
     * State when the task has not been scheduled.
     */
    static final int STATE_IDLE = 0;
    /**
     * State when the task has been scheduled and should not be re-scheduled until next executed.
     */
    static final int STATE_QUEUED = 1;
    /**
     * State when the task has been disposed and should no longer be scheduled.
     */
    static final int STATE_DISPOSED = 2;
    /**
     * Invalid state that should never be set.
     */
    static final int STATE_INVALID = 3;
    /**
     * Mask used to extract state bits.
     */
    private static final int STATE_MASK = STATE_IDLE | STATE_QUEUED | STATE_DISPOSED;

    /**
     * Return true if flags contains valid priority.
     *
     * @param flags the flags.
     * @return true if flags contains priority.
     */
    static boolean isStateValid( final int flags )
    {
      assert BrainCheckConfig.checkInvariants() || BrainCheckConfig.checkApiInvariants();
      return STATE_INVALID != ( STATE_MASK & flags );
    }

    static int setState( final int flags, final int state )
    {
      return ( ~STATE_MASK & flags ) | state;
    }

    static int getState( final int flags )
    {
      return STATE_MASK & flags;
    }

    /**
     * Return true if flags contains valid priority.
     *
     * @param flags the flags.
     * @return true if flags contains priority.
     */
    static boolean isPriorityValid( final int flags )
    {
      assert BrainCheckConfig.checkInvariants() || BrainCheckConfig.checkApiInvariants();
      final int priorityIndex = getPriorityIndex( flags );
      return priorityIndex <= 4 && priorityIndex >= 0;
    }

    /**
     * Extract and return the priority flag.
     * This method will not attempt to check priority value is valid.
     *
     * @param flags the flags.
     * @return the priority.
     */
    static int getPriority( final int flags )
    {
      return flags & PRIORITY_MASK;
    }

    /**
     * Extract and return the priority value ranging from the highest priority 0 and lowest priority 4.
     * This method assumes that flags has valid priority and will not attempt to re-check.
     *
     * @param flags the flags.
     * @return the priority.
     */
    static int getPriorityIndex( final int flags )
    {
      return ( getPriority( flags ) >> PRIORITY_SHIFT ) - 1;
    }

    static int priority( final int flags )
    {
      return 0 != getPriority( flags ) ? 0 : PRIORITY_NORMAL;
    }

    private Flags()
    {
    }
  }
}
