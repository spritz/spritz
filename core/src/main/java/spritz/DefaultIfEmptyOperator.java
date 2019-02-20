package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

final class DefaultIfEmptyOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final T _defaultValue;

  DefaultIfEmptyOperator( @Nonnull final Stream<T> upstream, @Nonnull final T defaultValue )
  {
    super( upstream );
    _defaultValue = Objects.requireNonNull( defaultValue );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends PassThroughSubscription<T, DefaultIfEmptyOperator<T>>
  {
    private boolean _itemEmitted;

    WorkerSubscription( @Nonnull final DefaultIfEmptyOperator<T> stream,
                        @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    public void onComplete()
    {
      if ( !_itemEmitted )
      {
        super.onNext( getStream()._defaultValue );
      }
      super.onComplete();
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      _itemEmitted = true;
      super.onNext( item );
    }
  }
}
