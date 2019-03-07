package spritz;

import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class CollectionStreamSource<T>
  extends Stream<T>
{
  private final Collection<T> _data;

  CollectionStreamSource( @Nullable final String name, @Nonnull final Collection<T> data )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "fromCollection", String.valueOf( data ) ) : null );
    _data = Objects.requireNonNull( data );
  }

  @Override
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    extends AbstractSubscription<T, CollectionStreamSource<T>>
  {
    WorkerSubscription( @Nonnull final CollectionStreamSource<T> stream,
                        @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    void pushData()
    {
      final Subscriber<? super T> subscriber = getSubscriber();
      for ( final T item : getStream()._data )
      {
        if ( isCancelled() )
        {
          return;
        }
        subscriber.onNext( item );
      }
      if ( isNotCancelled() )
      {
        subscriber.onComplete();
      }
    }
  }
}
