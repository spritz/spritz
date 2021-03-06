package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Subject<T>
  extends Hub<T, T>
{
  Subject( @Nullable final String name )
  {
    super( Spritz.areNamesEnabled() ? Stream.generateName( name, "subject" ) : null );
  }

  @Override
  void performNext( @Nonnull final T item )
  {
    downstreamNext( item );
  }

  @Override
  void performError( @Nonnull final Throwable error )
  {
    downstreamError( error );
    terminateUpstreamSubscribers();
  }

  @Override
  void performComplete()
  {
    downstreamComplete();
    terminateUpstreamSubscribers();
  }
}
