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

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( new PassThroughSubscription<>( this, subscriber ) );
    subscriber.onError( _error );
  }
}
