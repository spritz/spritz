package streak;

import javax.annotation.Nonnull;

public final class Streak
{
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

  @SafeVarargs
  public static <T> Flow.Stream<T> of( final T... values )
  {
    return new StaticPublisher<T>( values );
  }

  /**
   * Constructs a RangePublisher instance with the given start and count values
   * that yields a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   * @return the created publisher.
   */
  public static Flow.Stream<Integer> range( final int start, final int count )
  {
    return new RangePublisher( start, count );
  }

  @SafeVarargs
  public static <T> Flow.Stream<T> concat( @Nonnull final Flow.Stream<T>... upstreams )
  {
    return new ConcatPublisher<>( upstreams );
  }

  public static Flow.Stream<Integer> periodic( final int period )
  {
    return new PeriodicPublisher( period );
  }
}
