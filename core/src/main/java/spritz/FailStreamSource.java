package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class FailStreamSource<T>
  extends Stream<T>
{
  @Nonnull
  private final Throwable _error;

  FailStreamSource( @Nullable final String name, @Nonnull final Throwable error )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "fail" ) : null );
    _error = Objects.requireNonNull( error );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final PassThroughSubscription<T, FailStreamSource<T>> subscription =
      new PassThroughSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscriber.onError( _error );
    return subscription;
  }
}
