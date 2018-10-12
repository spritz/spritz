package streak.schedulers.m1;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Disposable;
import streak.Streak;
import static org.realityforge.braincheck.Guards.*;

/**
 * A task represents an executable element that can be ran by a task executor.
 */
public final class Task
  implements Disposable
{
  /**
   * A human consumable name for task. It should be non-null if {@link streak.Streak#areNamesEnabled()} returns
   * true and <tt>null</tt> otherwise.
   */
  @Nullable
  private final String _name;
  /**
   * The code to invoke when task is executed.
   */
  @Nonnull
  private final Runnable _task;
  /**
   * Flag set to true when the task has been scheduled and should not be re-scheduled until next executed.
   */
  private boolean _scheduled;
  /**
   * Flag set to true when the task has been disposed and should no longer be scheduled.
   */
  private boolean _disposed;

  Task( @Nullable final String name, @Nonnull final Runnable task )
  {
    if ( Streak.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> Streak.areNamesEnabled() || null == name,
                    () -> "Streak-0052: TaskEntry passed a name '" + name + "' but Streak.areNamesEnabled() " +
                          "returns false" );
    }
    _name = name;
    _task = Objects.requireNonNull( task );
  }

  /**
   * Return the name of the task.
   * This method should NOT be invoked unless {@link streak.Streak#areNamesEnabled()} returns true and will throw an
   * exception if invariant checking is enabled.
   *
   * @return the name of the task.
   */
  @Nonnull
  public final String getName()
  {
    if ( Streak.shouldCheckApiInvariants() )
    {
      apiInvariant( Streak::areNamesEnabled,
                    () -> "Streak-0053: TaskEntry.getName() invoked when Streak.areNamesEnabled() returns false" );
    }
    assert null != _name;
    return _name;
  }

  /**
   * Return the task.
   *
   * @return the task.
   */
  @Nonnull
  public Runnable getTask()
  {
    return _task;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void dispose()
  {
    if ( isNotDisposed() )
    {
      _disposed = true;
      _scheduled = false;
      //TODO:_scheduler.cancelTask( this );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDisposed()
  {
    return _disposed;
  }

  /**
   * Mark task as being scheduled, first verifying that it is not already scheduled.
   */
  void markAsScheduled()
  {
    if ( Streak.shouldCheckInvariants() )
    {
      invariant( () -> !_scheduled,
                 () -> "Streak-0022: Attempting to re-schedule task named '" + getName() +
                       "' when task is already scheduled." );
    }
    _scheduled = true;
  }

  /**
   * Clear the scheduled flag, first verifying that the task is scheduled.
   */
  void markAsExecuted()
  {
    if ( Streak.shouldCheckInvariants() )
    {
      invariant( () -> _scheduled,
                 () -> "Streak-0023: Attempting to clear scheduled flag on task named '" + getName() +
                       "' but task is not scheduled." );
    }
    _scheduled = false;
  }

  /**
   * Return true if task is already scheduled.
   *
   * @return true if task is already scheduled.
   */
  boolean isScheduled()
  {
    return _scheduled;
  }
}
