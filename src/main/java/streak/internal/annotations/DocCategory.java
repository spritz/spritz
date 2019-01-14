package streak.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

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
    CONSTRUCTION( false ),
    /**
     * Observing items and signals as they pass through the stream.
     */
    PEEKING( true ),
    /**
     * Operators that remove items from a stream based on some criteria.
     */
    FILTERING( true ),
    /**
     * Rate limiting operators.
     * Operators are typically {@link #FILTERING} operators except that the intent is to reduce the rate of items emitted.
     */
    RATE_LIMITING( true ),
    /**
     * Cutting the stream into sequential segments.
     * Operators are typically {@link #FILTERING} operators except that the intent is include or exclude sequences of items within the stream.
     */
    SLICING( true ),
    /**
     * Operators that change the form of items and signals passing through the stream.
     */
    TRANSFORMATION( true ),
    /**
     * Combining multiple streams into a single stream.
     */
    MERGING( true ),
    /**
     * Accumulate items and signals and emit events based on accumulation process.
     */
    ACCUMULATING( true ),
    /**
     * Unknown how we should categorize this operator at this stage.
     */
    UNKNOWN( true );
    /**
     * True if the category describes operators.
     */
    private final boolean _operator;

    Type( final boolean operator )
    {
      _operator = operator;
    }

    /**
     * Return true if the category describes operators.
     *
     * @return true if the category describes operators.
     */
    public boolean isOperator()
    {
      return _operator;
    }
  }

  /**
   * Return the categories where the operator is documented.
   *
   * @return the categories where the operator is documented.
   */
  @Nonnull
  Type[] value() default {};
}
