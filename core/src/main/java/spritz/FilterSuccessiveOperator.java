package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class FilterSuccessiveOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final SuccessivePredicate<T> _predicate;

  FilterSuccessiveOperator( @Nonnull final Stream<T> upstream, @Nonnull final SuccessivePredicate<T> predicate )
  {
    super( upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
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
