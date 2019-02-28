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

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( new PassThroughSubscription<>( this, subscriber ) );
  }
}
