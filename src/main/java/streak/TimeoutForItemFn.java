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
   * The value MUST NOT be negative but MAY BE zero which implies no timeout.
   *
   * @param item the item.
   * @return the timeout value.
   */
  int getTimeout( @Nonnull T item );
}
