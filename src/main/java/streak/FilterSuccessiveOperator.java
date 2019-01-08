package streak;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class FilterSuccessiveOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final SuccessivePredicate<T> _predicate;

  FilterSuccessiveOperator( @Nonnull final Stream<? extends T> upstream,
                            @Nonnull final SuccessivePredicate<T> predicate )
  {
    super( upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _predicate ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    @Nonnull
    private final SuccessivePredicate<T> _predicate;
    @Nullable
    private T _lastItem;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        @Nonnull final SuccessivePredicate<T> predicate )
    {
      super( subscriber );
      _predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( _predicate.filter( _lastItem, item ) )
      {
        return false;
      }
      else
      {
        _lastItem = item;
        return true;
      }
    }
  }
}
