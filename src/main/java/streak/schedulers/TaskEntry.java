package streak.schedulers;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Disposable;
import streak.Streak;
import static org.realityforge.braincheck.Guards.*;

/**
 * Entry for task scheduled by scheduler.
 */
final class TaskEntry
  implements Disposable
{
  @Nonnull
  private final BasicScheduler _scheduler;
  /**
   * A human consumable name for task. It should be non-null if {@link streak.Streak#areNamesEnabled()} returns
   * true and <tt>null</tt> otherwise.
   */
  @Nullable
  private final String _name;
  @Nonnull
  private final Runnable _task;
  /**
   * The period between scheduling task. If this value is 0 then the task will not be rescheduled.
   * The value should not be less than 0.
   */
  private final int _period;

  private boolean _disposed;
  private int _nextTime;

  TaskEntry( @Nonnull final BasicScheduler scheduler,
             @Nullable final String name,
             @Nonnull final Runnable task,
             final int period )
  {
    if ( Streak.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> Streak.areNamesEnabled() || null == name,
                    () -> "Streak-0052: TaskEntry passed a name '" + name + "' but Streak.areNamesEnabled() " +
                          "returns false" );
      apiInvariant( () -> period >= 0, () -> "Streak-0051: TaskEntry passed an invalid negative period " + period );
    }
    _scheduler = Objects.requireNonNull( scheduler );
    _name = name;
    _task = Objects.requireNonNull( task );
    _period = period;
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

  int getNextTime()
  {
    return _nextTime;
  }

  void setNextTime( final int nextTime )
  {
    _nextTime = nextTime;
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
      _scheduler.cancelTask( this );
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
}
