package streak;

import javax.annotation.Nonnull;

final class LimitOperator<T>
  extends AbstractStream<T>
{
  private final int _count;

  LimitOperator( @Nonnull final Stream<? extends T> upstream, final int count )
  {
    super( upstream );
    assert count > 0;
    _count = count;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _count ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    private int _remaining;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, final int remaining )
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
      if ( _remaining > 1 )
      {
        _remaining--;
        return true;
      }
      else if ( 1 == _remaining )
      {
        _remaining = 0;
        getDownstreamSubscriber().onNext( item );
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
