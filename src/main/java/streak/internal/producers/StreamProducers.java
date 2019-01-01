package streak.internal.producers;

import streak.Flow;

/**
 * Container for methods that produce a single stream.
 */
public interface StreamProducers
{
  /**
   * Creates a stream that emits the parameters as elements.
   *
   * @param <T> the type of elements contained in the stream.
   * @param values the values to emit.
   * @return the new stream.
   */
  @SuppressWarnings( "unchecked" )
  default <T> Flow.Stream<T> of( final T... values )
  {
    return new StaticPublisher<>( values );
  }

  /**
   * Creates a stream that emits no elements to the stream and immediately emits a completion notification.
   *
   * @param <T> the type of elements that the stream declared as containing (despite never containing any elements).
   * @return the new stream.
   */
  @SuppressWarnings( "unchecked" )
  default <T> Flow.Stream<T> empty()
  {
    return of();
  }

  /**
   * Create a stream that emits a sequence of numbers within a specified range.
   * The stream create a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   * @return the new stream.
   */
  default Flow.Stream<Integer> range( final int start, final int count )
  {
    return new RangePublisher( start, count );
  }

  /**
   * Create a stream that emits sequential numbers every specified interval of time.
   * The stream create a sequence of [start, start + count).
   *
   * @param period the period with which emit elements.
   * @return the new stream.
   */
  default Flow.Stream<Integer> periodic( final int period )
  {
    return new PeriodicPublisher( period );
  }
}
