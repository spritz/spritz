package spritz;

import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class DistinctOperator<T>
  extends AbstractStream<T, T>
{
  DistinctOperator( @Nullable final String name, @Nonnull final Stream<T> upstream )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "distinct" ) : null, upstream );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    getUpstream().subscribe( subscription );
    return subscription;
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

    @Override
    boolean shouldIncludeItem( @Nonnull final T item )
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
