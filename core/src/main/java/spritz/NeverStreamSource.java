package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class NeverStreamSource<T>
  extends Stream<T>
{
  NeverStreamSource( @Nullable final String name )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "never" ) : null );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final PassThroughSubscription<T, NeverStreamSource<T>> subscription =
      new PassThroughSubscription<>( this, subscriber );
    subscriber.onSubscribe( subscription );
    return subscription;

  }
}
