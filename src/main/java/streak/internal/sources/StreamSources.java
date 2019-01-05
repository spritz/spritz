package streak.internal.sources;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Flow;

/**
 * Container for methods that create a stream.
 */
public interface StreamSources
{
  /**
   * Creates a stream that emits the parameters as elements.
   *
   * @param <T>    the type of elements contained in the stream.
   * @param values the values to emit.
   * @return the new stream.
   */
  @SuppressWarnings( "unchecked" )
  default <T> Flow.Stream<T> of( @Nonnull final T... values )
  {
    return new StaticStreamSource<>( values );
  }

  /**
   * Creates a stream that emits no elements to the stream and immediately emits a completion signal.
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
   * Creates a stream that emits the value parameter as an element if the value is not null.
   *
   * @param <T>   the type of elements contained in the stream.
   * @param value the value to emit if non null.
   * @return the new stream.
   */
  @SuppressWarnings( "unchecked" )
  default <T> Flow.Stream<T> ofNullable( @Nullable final T value )
  {
    return null == value ? empty() : of( value );
  }

  /**
   * Creates a stream that emits no elements and immediately emits an error signal.
   * This is an alias for {@link #error(Throwable)}.
   *
   * @param <T>   the type of elements that the stream declared as containing (despite never containing any elements).
   * @param error the error to emit.
   * @return the new stream.
   * @see #error(Throwable)
   */
  default <T> Flow.Stream<T> fail( @Nonnull final Throwable error )
  {
    return new FailStreamSource<>( error );
  }

  /**
   * Creates a stream that emits no elements and immediately emits an error signal.
   * This is an alias for {@link #fail(Throwable)}.
   *
   * @param <T>   the type of elements that the stream declared as containing (despite never containing any elements).
   * @param error the error to emit.
   * @return the new stream.
   * @see #fail(Throwable)
   */
  default <T> Flow.Stream<T> error( @Nonnull final Throwable error )
  {
    return new FailStreamSource<>( error );
  }

  /**
   * Creates a stream that emits elements of the supplied collection.
   *
   * @param <T>    the type of elements contained in the stream.
   * @param values the collection of values to emit.
   * @return the new stream.
   */
  default <T> Flow.Stream<T> fromCollection( @Nonnull final Collection<T> values )
  {
    return new CollectionStreamSource<>( values );
  }

  /**
   * Creates a stream that emits elements from the supplied {@link java.util.stream.Stream}.
   *
   * @param <T>    the type of elements contained in the stream.
   * @param stream the java.util.stream.Stream stream of values to emit.
   * @return the new stream.
   */
  default <T> Flow.Stream<T> fromStream( @Nonnull final java.util.stream.Stream<T> stream )
  {
    return fromCollection( stream.collect( Collectors.toList() ) );
  }

  /**
   * Creates an infinite stream that emits elements from the {@link Callable} parameter.
   * The user must be very careful to add a subsequent stream stage that disposes the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of elements contained in the stream.
   * @param callable the function that generates values to emit.
   * @return the new stream.
   */
  default <T> Flow.Stream<T> fromCallable( @Nonnull final Callable<T> callable )
  {
    return new GenerateStreamSource<>( callable );
  }

  /**
   * Creates an infinite stream that emits elements from the {@link Supplier} parameter.
   * The user must be very careful to add a subsequent stream stage that disposes the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of elements contained in the stream.
   * @param supplier the function that generates values to emit.
   * @return the new stream.
   */
  default <T> Flow.Stream<T> fromSupplier( @Nonnull final Supplier<T> supplier )
  {
    return new GenerateStreamSource<>( supplier::get );
  }

  /**
   * Creates a stream that completes when the {@link Runnable} parameter completes running.
   * The stream will signal an error if the runnable generates an error while running.
   *
   * @param <T>      the type of elements that the stream declared as containing (despite never containing any elements).
   * @param runnable the runnable to execute.
   * @return the new stream.
   */
  default <T> Flow.Stream<T> fromRunnable( @Nonnull final Runnable runnable )
  {
    return new RunnableStreamSource<>( runnable );
  }

  /**
   * Creates an infinite stream that emits elements from the {@link Supplier} parameter at specified period.
   * The user must be very careful to add a subsequent stream stage that disposes the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of elements contained in the stream.
   * @param supplier the function that generates values to emit.
   * @param period   the period with which elements are emitted.
   * @return the new stream.
   */
  default <T> Flow.Stream<T> generate( @Nonnull final Supplier<T> supplier, final int period )
  {
    return periodic( period ).map( e -> supplier.get() );
  }

  /**
   * Creates a stream that emits no elements, never completes and never fails.
   *
   * @param <T> the type of elements that the stream declared as containing (despite never containing any elements).
   * @return the new stream.
   */
  default <T> Flow.Stream<T> never()
  {
    return new NeverStreamSource<>();
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
    return new RangeStreamSource( start, count );
  }

  /**
   * Create a stream that emits sequential numbers every specified interval of time.
   * The stream create a sequence of [start, start + count).
   *
   * @param period the period with which elements are emitted.
   * @return the new stream.
   */
  default Flow.Stream<Integer> periodic( final int period )
  {
    return new PeriodicStreamSource( period );
  }

  @SuppressWarnings( "unchecked" )
  default <T> Flow.Stream<T> concat( @Nonnull final Flow.Stream<T>... upstreams )
  {
    return of( upstreams ).concatMap( v -> v );
  }

  @SuppressWarnings( "unchecked" )
  default <T> Flow.Stream<T> merge( @Nonnull final Flow.Stream<T>... upstreams )
  {
    return of( upstreams ).mergeMap( v -> v );
  }
}
