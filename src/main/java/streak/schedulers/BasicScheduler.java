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
  public Disposable scheduleAtFixedRate( @Nullable final String name,
                                         @Nonnull final Runnable task,
                                         final int initialDelay,
                                         final int period )
  {
    final TaskEntry entry = new TaskEntry( this, name, task, period );
    entry.setNextTime( _now + initialDelay );
    _queue.add( entry );
    return entry;
  }

  final void cancelTask( @Nonnull final TaskEntry taskEntry )
  {
    //TODO: Remove?
  }
}
