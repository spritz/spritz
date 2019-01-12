package streak;

import javax.annotation.Nonnull;
import jsinterop.annotations.JsFunction;

/**
 * Functional interface for determining {@code timeout} for particular item.
 * The semantics of the {@code timeout} value depends upon where this interface is used.
 */
@FunctionalInterface
@JsFunction
public interface TimeoutForItemFn<T>
{
  /**
   * Return the timeout associated with this item.
   *
   * @param item the item.
   * @return the timeout value.
   */
  int getTimeout( @Nonnull T item );
}
