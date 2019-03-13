package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class LimitOperator<T>
  extends AbstractStream<T, T>
{
  private final int _count;

  LimitOperator( @Nullable final String name, @Nonnull final Stream<T> upstream, final int count )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "limit", String.valueOf( count ) ) : null, upstream );
    assert count > 0;
    _count = count;
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
    extends AbstractFilterSubscription<T, LimitOperator<T>>
  {
    private int _remaining;

    WorkerSubscription( @Nonnull final LimitOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
      _remaining = stream._count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( _remaining > 1 )
      {
        _remaining--;
        return true;
      }
      else if ( 1 == _remaining )
      {
        _remaining = 0;
        getSubscriber().onNext( item );
      }
      doComplete();
      return false;
    }

    private void doComplete()
    {
      getUpstream().cancel();
      onComplete();
    }
  }
}
