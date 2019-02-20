package spritz;

import javax.annotation.Nonnull;

final class SkipOperator<T>
  extends AbstractStream<T, T>
{
  private final int _count;

  SkipOperator( @Nonnull final Stream<T> upstream, final int count )
  {
    super( upstream );
    assert count > 0;
    _count = count;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T, SkipOperator<T>>
  {
    private int _remaining;

    WorkerSubscription( @Nonnull final SkipOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
      _remaining = stream._count;
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
        return false;
      }
      else
      {
        return true;
      }
    }
  }
}
