package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

abstract class AbstractEventEmitter<T>
  implements EventEmitter<T>
{
  @Nullable
  private Throwable _error;
  private boolean _complete;

  /**
   * {@inheritDoc}
   */
  @Override
  public final void next( @Nonnull final T item )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      invariant( () -> null == _error,
                 () -> "Spritz-0023: EventEmitter.next(...) invoked after EventEmitter.error(...) invoked." );
      invariant( () -> !_complete,
                 () -> "Spritz-0024: EventEmitter.next(...) invoked after EventEmitter.complete() invoked." );
    }
    doNext( item );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void error( @Nonnull final Throwable error )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      invariant( () -> null == _error,
                 () -> "Spritz-0025: EventEmitter.error(...) invoked after EventEmitter.error(...) invoked." );
      invariant( () -> !_complete,
                 () -> "Spritz-0026: EventEmitter.error(...) invoked after EventEmitter.complete() invoked." );
    }
    _error = error;
    doError( error );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void complete()
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      invariant( () -> null == _error,
                 () -> "Spritz-0027: EventEmitter.complete(...) invoked after EventEmitter.error(...) invoked." );
      invariant( () -> !_complete,
                 () -> "Spritz-0028: EventEmitter.complete(...) invoked after EventEmitter.complete() invoked." );
    }
    _complete = true;
    doComplete();
  }

  @Nullable
  protected final Throwable getError()
  {
    return _error;
  }

  protected final boolean isComplete()
  {
    return _complete;
  }

  protected abstract void doNext( @Nonnull T item );

  protected abstract void doError( @Nonnull Throwable error );

  protected abstract void doComplete();
}
