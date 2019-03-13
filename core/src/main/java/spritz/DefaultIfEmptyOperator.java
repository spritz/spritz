package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class DefaultIfEmptyOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final T _defaultValue;

  DefaultIfEmptyOperator( @Nullable final String name,
                          @Nonnull final Stream<T> upstream,
                          @Nonnull final T defaultValue )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "defaultIfEmpty", String.valueOf( defaultValue ) ) : null,
           upstream );
    _defaultValue = Objects.requireNonNull( defaultValue );
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
