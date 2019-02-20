package spritz;

import java.util.HashSet;
import javax.annotation.Nonnull;

final class DistinctOperator<T>
  extends AbstractStream<T, T>
{
  DistinctOperator( @Nonnull final Stream<T> upstream )
  {
    super( upstream );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T, DistinctOperator<T>>
  {
    @Nonnull
    private final HashSet<T> _emitted = new HashSet<>();

    WorkerSubscription( @Nonnull final DistinctOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
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
