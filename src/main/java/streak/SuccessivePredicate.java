package streak;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Functional interface for determining whether an item should be dropped based on last emitted item.
 */
@FunctionalInterface
public interface SuccessivePredicate<T>
{
  /**
   * Return true to emit candidateItem.
   *
   * @param lastEmittedItem the item that was emitted last. This may be null if no item has been emitted yet.
   * @param candidateItem   the current item.
   * @return true to emit candidateItem, false to drop candidateItem.
   */
  boolean filter( @Nullable T lastEmittedItem, @Nonnull T candidateItem );
}
