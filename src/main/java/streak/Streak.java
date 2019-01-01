package streak;

import javax.annotation.Nonnull;
import streak.internal.producers.StreamProducers;

public final class Streak
{
  private static final StreakStreamProducers PRODUCERS = new StreakStreamProducers();

  private Streak()
  {
  }

  public static boolean areNamesEnabled()
  {
    //TODO: Convert this into compile-time constraint.
    return true;
  }

  public static boolean shouldCheckInvariants()
  {
    //TODO: Convert this into compile-time constraint.
    return true;
  }

  public static boolean shouldCheckApiInvariants()
  {
    //TODO: Convert this into compile-time constraint.
    return true;
  }

  /**
   * Creates a stream that emits the parameters as elements.
   *
   * @param values the values to emit.
   * @return the new stream.
   */
  @SafeVarargs
  public static <T> Flow.Stream<T> of( final T... values )
  {
    return PRODUCERS.of( values );
  }

  /**
   * Creates a stream that emits no elements to the stream and immediately emits a completion notification.
   *
   * @return the new stream.
   */
  public static <T> Flow.Stream<T> empty()
  {
    return PRODUCERS.empty();
  }

  /**
   * Create a stream that emits a sequence of numbers within a specified range.
   * The stream create a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   * @return the new stream.
   */
  public static Flow.Stream<Integer> range( final int start, final int count )
  {
    return PRODUCERS.range( start, count );
  }

  /**
   * Create a stream that emits sequential numbers every specified interval of time.
   * The stream create a sequence of [start, start + count).
   *
   * @param period the period with which emit elements.
   * @return the new stream.
   */
  public static Flow.Stream<Integer> periodic( final int period )
  {
    return PRODUCERS.periodic( period );
  }

  @SafeVarargs
  public static <T> Flow.Stream<T> concat( @Nonnull final Flow.Stream<T>... upstreams )
  {
    return new ConcatPublisher<>( upstreams );
  }

  private static final class StreakStreamProducers
    implements StreamProducers
  {
  }
}
