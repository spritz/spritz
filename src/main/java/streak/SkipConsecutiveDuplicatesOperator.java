package streak;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class SkipConsecutiveDuplicatesOperator<T>
  extends PublisherWithUpstream<T>
{
  SkipConsecutiveDuplicatesOperator( @Nonnull final Flow.Stream<? extends T> upstream )
  {
    super( upstream );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    @Nullable
    private T _lastItem;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber )
    {
      super( subscriber );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( Objects.equals( item, _lastItem ) )
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
