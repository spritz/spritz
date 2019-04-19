package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class FilterSuccessiveOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final SuccessivePredicate<T> _predicate;

  FilterSuccessiveOperator( @Nullable final String name,
                            @Nonnull final Stream<T> upstream,
                            @Nonnull final SuccessivePredicate<T> predicate )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "errorIfEmpty" ) : null, upstream );
    _predicate = Objects.requireNonNull( predicate );
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
    extends AbstractFilterSubscription<T, FilterSuccessiveOperator<T>>
  {
    @Nullable
    private T _lastItem;

    WorkerSubscription( @Nonnull final FilterSuccessiveOperator<T> stream,
                        @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( getStream()._predicate.filter( _lastItem, item ) )
      {
        _lastItem = item;
        return true;
      }
      else
      {
        return false;
      }
    }
  }
}
