package streak;

import javax.annotation.Nonnull;

final class LimitOperator<T>
  extends PublisherWithUpstream<T>
{
  private final int _count;

  LimitOperator( @Nonnull final Flow.Stream<? extends T> upstream, final int count )
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
        return true;
      }
      else
      {
        getUpstream().dispose();
        onComplete();
        return false;
      }
    }
  }
}
