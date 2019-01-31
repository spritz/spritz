package spritz.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;
import spritz.VirtualProcessorUnit;

/**
 * Category where operator resides in documentation.
 */
@Target( ElementType.METHOD )
public @interface DocCategory
{
  enum Type
  {
    /**
     * Methods to construct a stream.
     */
    @SourceCategory
    CONSTRUCTION,
    /**
     * Observing items and signals as they pass through the stream.
     */
    PEEKING,
    /**
     * Operators that remove items from a stream based on some criteria.
     */
    FILTERING,
    /**
     * Rate limiting operators.
     * Operators are typically {@link #FILTERING} operators except that the intent is to reduce the rate of items emitted.
     */
    RATE_LIMITING,
    /**
     * Cutting the stream into sequential segments.
     * Operators are typically {@link #FILTERING} operators except that the intent is include or exclude sequences of items within the stream.
     */
    SLICING,
    /**
     * Operators that change the form of items and signals passing through the stream.
     */
    TRANSFORMATION,
    /**
     * Combining multiple streams into a single stream.
     */
    MERGING,
    /**
     * Accumulate items and signals and emit events based on accumulation process.
     */
    ACCUMULATING,
    /**
     * Operators that change the {@link VirtualProcessorUnit} on which events and signals are emitted or the time at which events and signals are emitted.
     */
    SCHEDULING,
    /**
     * Unknown how we should categorize this operator at this stage.
     */
    UNKNOWN
  }

  /**
   * Return the categories where the operator is documented.
   *
   * @return the categories where the operator is documented.
   */
  @Nonnull
  Type[] value() default {};
}
