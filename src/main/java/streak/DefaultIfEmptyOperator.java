package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

final class DefaultIfEmptyOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final T _defaultValue;

  DefaultIfEmptyOperator( @Nonnull final Stream<? extends T> upstream, @Nonnull final T defaultValue )
  {
    super( upstream );
    _defaultValue = Objects.requireNonNull( defaultValue );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _defaultValue ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<T>
  {
    @Nonnull
    private final T _defaultValue;
    private boolean _itemEmitted;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber, @Nonnull final T defaultValue )
    {
      super( subscriber );
      _defaultValue = defaultValue;
    }

    @Override
    public void onComplete()
    {
      if ( !_itemEmitted )
      {
        super.onNext( _defaultValue );
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
