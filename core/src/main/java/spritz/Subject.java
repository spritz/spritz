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

  /**
   * {@inheritDoc}
   */
  @Override
  void performNext( @Nonnull final T item )
  {
    Scheduler.current( () -> downstreamNext( item ) );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  void performError( @Nonnull final Throwable error )
  {
    Scheduler.current( () -> downstreamError( error ) );
    terminateUpstreamSubscribers();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  void performComplete()
  {
    Scheduler.current( this::downstreamComplete );
    terminateUpstreamSubscribers();
  }
}
