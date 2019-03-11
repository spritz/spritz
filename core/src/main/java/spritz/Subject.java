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
  public final void next( @Nonnull final T item )
  {
    ensureNextValid();
    Scheduler.current( () -> doNext( item ) );
  }
}
