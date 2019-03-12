package spritz;

import javax.annotation.Nonnull;

/**
 * Interface for elements that can emit events.
 */
public interface EventEmitter<T>
{
  /**
   * Emit a data item.
   * This method will be ignored if {@link #isDone()} returns <code>true</code>.
   *
   * @param item the data item.
   */
  void next( @Nonnull T item );

  /**
   * Emit an error signal.
   * This method will be ignored if {@link #isDone()} returns <code>true</code>.
   *
   * @param error the error.
   */
  void error( @Nonnull Throwable error );

  /**
   * Emit a complete signal.
   * This method will be ignored if {@link #isDone()} returns <code>true</code>.
   */
  void complete();

  /**
   * Return true if the emitter has emitted an error signal, a complete signal or has been cancelled.
   *
   * @return true if the emitter has emitted an error signal, a complete signal or has been cancelled.
   */
  boolean isDone();

  /**
   * Return true if {@link #isDone()} returns <code>false</code>.
   *
   * @return true if {@link #isDone()} returns <code>false</code>.
   */
  default boolean isNotDone()
  {
    return !isDone();
  }
}
