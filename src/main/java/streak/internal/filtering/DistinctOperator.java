package streak.internal.filtering;

import java.util.HashSet;
import javax.annotation.Nonnull;
import streak.Stream;
import streak.Subscriber;
import streak.internal.StreamWithUpstream;

final class DistinctOperator<T>
  extends StreamWithUpstream<T>
{
  DistinctOperator( @Nonnull final Stream<? extends T> upstream )
  {
    super( upstream );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    @Nonnull
    private final HashSet<T> _emitted = new HashSet<>();

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber )
    {
      super( subscriber );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( _emitted.contains( item ) )
      {
        return false;
      }
      else
      {
        _emitted.add( item );
        return true;
      }
    }
  }
}
