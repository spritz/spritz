package streak.internal.filtering;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.StreamWithUpstream;

final class SkipOperator<T>
  extends StreamWithUpstream<T>
{
  private final int _count;

  SkipOperator( @Nonnull final Flow.Stream<? extends T> upstream, final int count )
  {
    super( upstream );
    assert count > 0;
    _count = count;
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _count ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    private int _remaining;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, final int remaining )
    {
      super( subscriber );
      _remaining = remaining;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( _remaining > 0 )
      {
        _remaining--;
        return false;
      }
      else
      {
        return true;
      }
    }
  }
}
