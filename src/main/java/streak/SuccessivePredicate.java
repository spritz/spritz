package streak;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Functional interface for determining whether element should be dropped based on last emitted element.
 */
@FunctionalInterface
public interface SuccessivePredicate<T>
{
  /**
   * Return true to emit candidateElement.
   *
   * @param lastEmittedElement the element that was emitted last. This may be null if no element has been emitted yet.
   * @param candidateElement   the curremt element.
   * @return true to emit candidateElement, false to drop candidateElement.
   */
  boolean filter( @Nullable T lastEmittedElement, @Nonnull T candidateElement );
}
