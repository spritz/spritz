package spritz;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class StaticStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final T[] _data;

  StaticStreamSource( @Nullable final String name, @Nonnull final T[] data )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "of", arrayToString( data ) ) : null );
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  private static <T> String arrayToString( @Nonnull final T[] data )
  {
    final String str = Arrays.asList( data ).toString();
    return str.substring( 1, str.length() - 1 );
  }

  @Override
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    extends AbstractSubscription<T, StaticStreamSource<T>>
  {
    WorkerSubscription( @Nonnull final StaticStreamSource<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    void pushData()
    {
      final T[] data = getStream()._data;
      int offset = 0;
      while ( offset < data.length && isNotCancelled() )
      {
        final T item = data[ offset ];
        offset++;
        getSubscriber().onNext( item );
      }
      if ( isNotCancelled() )
      {
        getSubscriber().onComplete();
      }
    }
  }
}
