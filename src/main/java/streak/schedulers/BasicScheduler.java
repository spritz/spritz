package streak.schedulers;

import java.util.Comparator;
import java.util.PriorityQueue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Disposable;

public final class BasicScheduler
  implements Scheduler
{
  private final PriorityQueue<TaskEntry> _queue =
    new PriorityQueue<>( 10, Comparator.comparingInt( TaskEntry::getNextTime ) );
  private int _now = 0;

  /**
   * {@inheritDoc}
   */
  @Override
  public int now()
  {
    return _now;
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public Disposable schedule( @Nullable final String name, @Nonnull final Runnable task, final int delayInMillis )
  {
    return scheduleAtFixedRate( name, task, delayInMillis, 0 );
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public Disposable scheduleAtFixedRate( @Nullable final String name,
                                         @Nonnull final Runnable task,
                                         final int initialDelayInMillis,
                                         final int periodInMillis )
  {
    final TaskEntry entry = new TaskEntry( this, name, task, periodInMillis );
    entry.setNextTime( _now + initialDelayInMillis );
    _queue.add( entry );
    return entry;
  }

  final void cancelTask( @Nonnull final TaskEntry taskEntry )
  {
    //TODO: Remove?
  }
}
