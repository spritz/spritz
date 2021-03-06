package spritz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.dom.WebSocketConfig;
import zemeckis.VirtualProcessorUnit;
import static org.realityforge.braincheck.Guards.*;

@SuppressWarnings( { "WeakerAccess", "unused" } )
@MetaDataSource
public abstract class Stream<T>
{
  /**
   * The maximum concurrency of {@link #mergeMap(Function)} operator that does not specify concurrency.
   * This value is high enough that it is expected to be effectively infinite while not causing numeric
   * overflow in either JS or java compile targets.
   */
  private static final int DEFAULT_MERGE_CONCURRENCY = 1024 * 1024;
  /**
   * A human consumable name for the stream. It should be non-null if {@link Spritz#areNamesEnabled()} returns
   * true and <tt>null</tt> otherwise.
   */
  @Nullable
  private final String _name;

  Stream( @Nullable final String name )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> Spritz.areNamesEnabled() || null == name,
                    () -> "Spritz-0052: Stream passed a name '" + name + "' but Spritz.areNamesEnabled() is false" );
    }
    _name = Spritz.areNamesEnabled() ? Objects.requireNonNull( name ) : null;
  }

  /**
   * Creates a stream that emits the parameters as items and then emits the completion signal.
   *
   * @param <T>    the type of items contained in the stream.
   * @param values the values to emit.
   * @return the new stream.
   */
  @SuppressWarnings( "varargs" )
  @SafeVarargs
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> of( @Nonnull final T... values )
  {
    return of( null, values );
  }

  /**
   * Creates a stream that emits the parameters as items and then emits the completion signal.
   *
   * @param <T>    the type of items contained in the stream.
   * @param name   a human consumable name for the stream.
   * @param values the values to emit.
   * @return the new stream.
   */
  @SuppressWarnings( "varargs" )
  @SafeVarargs
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> of( @Nullable final String name, @Nonnull final T... values )
  {
    return new StaticStreamSource<>( name, values );
  }

  /**
   * Creates a stream that emits no items and immediately emits a completion signal.
   *
   * @param <T> the type of items that the stream declared as containing (despite never containing any items).
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> empty()
  {
    return empty( null );
  }

  /**
   * Creates a stream that emits no items and immediately emits a completion signal.
   *
   * @param <T>  the type of items that the stream declared as containing (despite never containing any items).
   * @param name a human consumable name for the stream.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> empty( @Nullable final String name )
  {
    return of( Spritz.areNamesEnabled() ? generateName( name, "empty" ) : null );
  }

  /**
   * Creates a stream that emits the value parameter as an item if the value is not null.
   *
   * @param <T>   the type of items contained in the stream.
   * @param value the value to emit if non null.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> ofNullable( @Nullable final T value )
  {
    return null == value ? empty() : of( value );
  }

  /**
   * Creates a stream that emits no items and immediately emits an error signal.
   *
   * @param <T>   the type of items that the stream declared as containing (despite never containing any items).
   * @param error the error to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fail( @Nonnull final Throwable error )
  {
    return fail( null, error );
  }

  /**
   * Creates a stream that emits no items and immediately emits an error signal.
   *
   * @param <T>   the type of items that the stream declared as containing (despite never containing any items).
   * @param name  a human consumable name for the stream.
   * @param error the error to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fail( @Nullable final String name, @Nonnull final Throwable error )
  {
    return new FailStreamSource<>( name, error );
  }

  /**
   * Creates a stream that emits items of the supplied collection.
   *
   * @param <T>    the type of items contained in the stream.
   * @param values the collection of values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromCollection( @Nonnull final Collection<T> values )
  {
    return fromCollection( null, values );
  }

  /**
   * Creates a stream that emits items of the supplied collection.
   *
   * @param <T>    the type of items contained in the stream.
   * @param name   a human consumable name for the stream.
   * @param values the collection of values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromCollection( @Nullable final String name, @Nonnull final Collection<T> values )
  {
    return new CollectionStreamSource<>( name, values );
  }

  /**
   * Creates a stream that emits items from the supplied {@link java.util.stream.Stream}.
   *
   * @param <T>    the type of items contained in the stream.
   * @param stream the java.util.stream.Stream stream of values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromStream( @Nonnull final java.util.stream.Stream<T> stream )
  {
    return fromCollection( stream.collect( Collectors.toList() ) );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Callable} parameter.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param callable the function that generates values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromCallable( @Nonnull final Callable<T> callable )
  {
    return fromCallable( null, callable );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Callable} parameter.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param name     a human consumable name for the stream.
   * @param callable the function that generates values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromCallable( @Nullable final String name, @Nonnull final Callable<T> callable )
  {
    return new CallableStreamSource<>( name, callable );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Supplier} parameter.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param supplier the function that generates values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromSupplier( @Nonnull final Supplier<T> supplier )
  {
    return fromSupplier( null, supplier );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Supplier} parameter.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param name     a human consumable name for the stream.
   * @param supplier the function that generates values to emit.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromSupplier( @Nullable final String name, @Nonnull final Supplier<T> supplier )
  {
    return fromCallable( Spritz.areNamesEnabled() ? generateName( name, "fromSupplier" ) : null, supplier::get );
  }

  /**
   * Creates a stream that completes when the {@link Runnable} parameter completes running.
   * The stream will signal an error if the runnable generates an error while running.
   *
   * @param <T>      the type of items that the stream declared as containing (despite never containing any items).
   * @param runnable the runnable to execute.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromRunnable( @Nonnull final Runnable runnable )
  {
    return fromRunnable( null, runnable );
  }

  /**
   * Creates a stream that completes when the {@link Runnable} parameter completes running.
   * The stream will signal an error if the runnable generates an error while running.
   *
   * @param <T>      the type of items that the stream declared as containing (despite never containing any items).
   * @param name     a human consumable name for the stream.
   * @param runnable the runnable to execute.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> fromRunnable( @Nullable final String name, @Nonnull final Runnable runnable )
  {
    return new RunnableStreamSource<>( name, runnable );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Supplier} parameter at specified period.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param supplier the function that generates values to emit.
   * @param period   the period with which items are emitted.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> generate( @Nonnull final Supplier<T> supplier, final int period )
  {
    return generate( null, supplier, period );
  }

  /**
   * Creates an infinite stream that emits items from the {@link Supplier} parameter at specified period.
   * The user must be very careful to add a subsequent stream stage that cancels the stream
   * otherwise this source will result in an infinite loop.
   *
   * @param <T>      the type of items contained in the stream.
   * @param name     a human consumable name for the stream.
   * @param supplier the function that generates values to emit.
   * @param period   the period with which items are emitted.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> generate( @Nullable final String name,
                                        @Nonnull final Supplier<T> supplier,
                                        final int period )
  {
    return periodic( period ).map( Spritz.areNamesEnabled() ? generateName( name, "generate" ) : null,
                                   e -> supplier.get() );
  }

  /**
   * Creates a stream that emits no items, never completes and never fails.
   *
   * @param <T> the type of items that the stream declared as containing (despite never containing any items).
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> never()
  {
    return never( null );
  }

  /**
   * Creates a stream that emits no items, never completes and never fails.
   *
   * @param <T>  the type of items that the stream declared as containing (despite never containing any items).
   * @param name a human consumable name for the stream.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> never( @Nullable final String name )
  {
    return new NeverStreamSource<>( name );
  }

  /**
   * Create a stream that emits a sequence of numbers within a specified range.
   * The stream create a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static Stream<Integer> range( final int start, final int count )
  {
    return range( null, start, count );
  }

  /**
   * Create a stream that emits a sequence of numbers within a specified range.
   * The stream create a sequence of [start, start + count).
   *
   * @param name  a human consumable name for the stream.
   * @param start the starting value of the range
   * @param count the number of items to emit
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static Stream<Integer> range( @Nullable final String name, final int start, final int count )
  {
    return new RangeStreamSource( name, start, count );
  }

  /**
   * Create a stream that emits sequential numbers every specified interval of time.
   * The stream create a sequence of [start, start + count).
   *
   * @param period the period with which items are emitted.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static Stream<Integer> periodic( final int period )
  {
    return periodic( null, period );
  }

  /**
   * Create a stream that emits sequential numbers every specified interval of time.
   * The stream create a sequence of [start, start + count).
   *
   * @param name   a human consumable name for the stream.
   * @param period the period with which items are emitted.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static Stream<Integer> periodic( @Nullable final String name, final int period )
  {
    return new PeriodicStreamSource( name, period );
  }

  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static WebSocketHub webSocket( @Nonnull final WebSocketConfig config )
  {
    return webSocket( null, config );
  }

  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static WebSocketHub webSocket( @Nullable final String name, @Nonnull final WebSocketConfig config )
  {
    return new WebSocketHub( name, config );
  }

  @SuppressWarnings( "varargs" )
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  @SafeVarargs
  public static <T> Stream<T> concat( @Nonnull final Stream<T>... upstreams )
  {
    return of( upstreams ).concatMap( v -> v );
  }

  @SuppressWarnings( "varargs" )
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  @SafeVarargs
  public static <T> Stream<T> merge( @Nonnull final Stream<T>... upstreams )
  {
    return of( upstreams ).mergeMap( v -> v );
  }

  /**
   * Creates a stream using a simple function.
   * THe function will simplify the creation of stream sources. In particular it eliminates the need to
   * maintain the state for subscription and will handle cancelled subscriptions by ignoring calls when in
   * cancelled state. While the code has a better developer experience, it may introduce a slightly worse runtime
   * experience.
   *
   * @param <T>            the type of items that the stream contains.
   * @param createFunction the function for creating the source.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> create( @Nonnull final SourceCreator<T> createFunction )
  {
    return create( null, createFunction );
  }

  /**
   * Creates a stream using a simple function.
   * THe function will simplify the creation of stream sources. In particular it eliminates the need to
   * maintain the state for subscription and will handle cancelled subscriptions by ignoring calls when in
   * cancelled state. While the code has a better developer experience, it may introduce a slightly worse runtime
   * experience.
   *
   * @param <T>            the type of items that the stream contains.
   * @param name           a human consumable name for the stream.
   * @param createFunction the function for creating the source.
   * @return the new stream.
   */
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> create( @Nullable final String name, @Nonnull final SourceCreator<T> createFunction )
  {
    return new CreateStreamSource<>( name, createFunction );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> subject()
  {
    return subject( null );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> subject( @Nullable final String name )
  {
    return new Subject<>( name );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> currentValueSubject( @Nonnull final T initialValue )
  {
    return currentValueSubject( null, initialValue );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> currentValueSubject( @Nullable final String name, @Nonnull final T initialValue )
  {
    return new CurrentValueSubject<>( name, initialValue );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubjectWithMaxAge( final int maxAge )
  {
    return replaySubjectWithMaxAge( null, maxAge );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubjectWithMaxAge( @Nullable final String name, final int maxAge )
  {
    return replaySubject( name, ReplaySubject.DEFAULT_VALUE, maxAge );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubjectWithMaxSize( final int maxSize )
  {
    return replaySubjectWithMaxSize( null, maxSize );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubjectWithMaxSize( @Nullable final String name, final int maxSize )
  {
    return replaySubject( name, maxSize, ReplaySubject.DEFAULT_VALUE );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubject( final int maxSize, final int maxAge )
  {
    return replaySubject( null, maxSize, maxAge );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubject( @Nullable final String name, final int maxSize, final int maxAge )
  {
    return new ReplaySubject<>( name, maxSize, maxAge );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubject()
  {
    return replaySubject( null );
  }

  @DocCategory( DocCategory.Type.SUBJECT )
  @Nonnull
  public static <T> Subject<T> replaySubject( @Nullable final String name )
  {
    return replaySubject( name, ReplaySubject.DEFAULT_VALUE, ReplaySubject.DEFAULT_VALUE );
  }

  /**
   * Subscribe the eventEmitter to this stream and forward events from the stream to the emitter.
   *
   * @param eventEmitter the eventEmitter.
   * @return the subscription.
   */
  @Nonnull
  public final Subscription subscribe( @Nonnull final EventEmitter<T> eventEmitter )
  {
    return subscribe( new ForwardToEventEmitterSubscriber<>( eventEmitter ) );
  }

  /**
   * Subscribe the subscriber to this stream so that it can receive events.
   *
   * @param subscriber the subscriber.
   * @return the subscription.
   */
  @Nonnull
  public final Subscription subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    return doSubscribe( Spritz.shouldValidateSubscriptions() ? new ValidatingSubscriber<>( subscriber ) : subscriber );
  }

  @Nonnull
  abstract Subscription doSubscribe( @Nonnull Subscriber<? super T> subscriber );

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling subscription.
   *
   * @param action the function called before signalling subscription.
   * @return the new stream.
   * @see #afterSubscribe(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekSubscribe( @Nonnull final Consumer<Subscription> action )
  {
    return peekSubscribe( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling subscription.
   *
   * @param name   the name specified by the user.
   * @param action the function called before signalling subscription.
   * @return the new stream.
   * @see #afterSubscribe(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekSubscribe( @Nullable final String name, @Nonnull final Consumer<Subscription> action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "onSubscribe" ) : null,
                                             s,
                                             action,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling subscription.
   *
   * @param action the function called after signalling subscription.
   * @return the new stream.
   * @see #peekSubscribe(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterSubscribe( @Nonnull final Consumer<Subscription> action )
  {
    return afterSubscribe( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling subscription.
   *
   * @param name   the name specified by the user.
   * @param action the function called after signalling subscription.
   * @return the new stream.
   * @see #peekSubscribe(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterSubscribe( @Nullable final String name, @Nonnull final Consumer<Subscription> action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "afterSubscribe" ) : null,
                                             s,
                                             null,
                                             action,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #peek(String, Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peek( @Nonnull final Consumer<? super T> action )
  {
    return peek( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #peek(Consumer)}.
   *
   * @param name   the name specified by the user.
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #peek(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peek( @Nullable final String name, @Nonnull final Consumer<? super T> action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "peek" ) : null,
                                             s,
                                             null,
                                             null,
                                             action,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after each item is emitted. This method is an alias for {@link #peek(Consumer)}.
   *
   * @param action the function after each item is emitted.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterNext( @Nonnull final Consumer<? super T> action )
  {
    return afterNext( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after each item is emitted. This method is an alias for {@link #peek(Consumer)}.
   *
   * @param name   the name specified by the user.
   * @param action the function after each item is emitted.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterNext( @Nullable final String name, @Nonnull final Consumer<? super T> action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "afterNext" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             action,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling error.
   *
   * @param action the function called before signalling error.
   * @return the new stream.
   * @see #afterError(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekError( @Nonnull final Consumer<Throwable> action )
  {
    return peekError( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling error.
   *
   * @param name   the name specified by the user.
   * @param action the function called before signalling error.
   * @return the new stream.
   * @see #afterError(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekError( @Nullable final String name, @Nonnull final Consumer<Throwable> action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "onError" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             action,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling error.
   *
   * @param action the function called after signalling error.
   * @return the new stream.
   * @see #peekError(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterError( @Nonnull final Consumer<Throwable> action )
  {
    return afterError( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling error.
   *
   * @param name   the name specified by the user.
   * @param action the function called after signalling error.
   * @return the new stream.
   * @see #peekError(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterError( @Nullable final String name, @Nonnull final Consumer<Throwable> action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "afterError" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             action,
                                             null,
                                             null,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete.
   *
   * @param action the function called when the stream completes.
   * @return the new stream.
   * @see #afterComplete(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekComplete( @Nonnull final Runnable action )
  {
    return peekComplete( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete.
   *
   * @param name   the name specified by the user.
   * @param action the function called when the stream completes.
   * @return the new stream.
   * @see #afterComplete(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekComplete( @Nullable final String name, @Nonnull final Runnable action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "onComplete" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             action,
                                             null,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete.
   *
   * @param action the function called when the stream completes.
   * @return the new stream.
   * @see #peekComplete(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterComplete( @Nonnull final Runnable action )
  {
    return afterComplete( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete.
   *
   * @param name   the name specified by the user.
   * @param action the function called when the stream completes.
   * @return the new stream.
   * @see #peekComplete(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterComplete( @Nullable final String name, @Nonnull final Runnable action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "afterComplete" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             action,
                                             null,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before the stream is canceled by a downstream stage.
   *
   * @param action the function called before the stream is canceled by a downstream stage.
   * @return the new stream.
   * @see #afterCancel(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekCancel( @Nonnull final Runnable action )
  {
    return peekCancel( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before the stream is canceled by a downstream stage.
   *
   * @param name   the name specified by the user.
   * @param action the function called before the stream is canceled by a downstream stage.
   * @return the new stream.
   * @see #afterCancel(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekCancel( @Nullable final String name, @Nonnull final Runnable action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "onCancel" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             action,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after the stream is canceled by a downstream stage.
   *
   * @param action the function called after the stream is canceled by a downstream stage.
   * @return the new stream.
   * @see #peekCancel(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterCancel( @Nonnull final Runnable action )
  {
    return afterCancel( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after the stream is canceled by a downstream stage.
   *
   * @param name   the name specified by the user.
   * @param action the function called after the stream is canceled by a downstream stage.
   * @return the new stream.
   * @see #peekCancel(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterCancel( @Nullable final String name, @Nonnull final Runnable action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "afterCancel" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             action ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #peekError(Consumer)} and
   * {@link #peekComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param action the function called before signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #afterTerminate(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekTerminate( @Nonnull final Runnable action )
  {
    return peekTerminate( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #peekError(Consumer)} and
   * {@link #peekComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param name   the name specified by the user.
   * @param action the function called before signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #afterTerminate(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peekTerminate( @Nullable final String name, @Nonnull final Runnable action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "peekTerminate" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             e -> action.run(),
                                             null,
                                             action,
                                             null,
                                             action,
                                             null ) );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #peekError(Consumer)} and
   * {@link #peekComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param action the function called after signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #peekTerminate(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterTerminate( @Nonnull final Runnable action )
  {
    return afterTerminate( null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #peekError(Consumer)} and
   * {@link #peekComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param name   the name specified by the user.
   * @param action the function called after signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #peekTerminate(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterTerminate( @Nullable final String name, @Nonnull final Runnable action )
  {
    return compose( s -> new PeekOperator<>( Spritz.areNamesEnabled() ? generateName( name, "afterTerminate" ) : null,
                                             s,
                                             null,
                                             null,
                                             null,
                                             null,
                                             null,
                                             e -> action.run(),
                                             null,
                                             action,
                                             null,
                                             action ) );
  }

  /**
   * Filter the items emitted by this stream using the specified {@link Predicate}.
   * Any items that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other items will be skipped.
   *
   * @param predicate the predicate to apply to each item.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> filter( @Nonnull final Predicate<? super T> predicate )
  {
    return filter( null, predicate );
  }

  /**
   * Filter the items emitted by this stream using the specified {@link Predicate}.
   * Any items that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other items will be skipped.
   *
   * @param name      the name specified by the user.
   * @param predicate the predicate to apply to each item.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> filter( @Nullable final String name, @Nonnull final Predicate<? super T> predicate )
  {
    return compose( s -> new PredicateFilterStream<>( name, s, predicate ) );
  }

  /**
   * Remove items in the stream that are not instances of the specified {@code type} and return a stream of the specified type.
   *
   * @param <DownstreamT> the type of item emitted downstream.
   * @param type          the class of items to be emitted downstream.
   * @return the new stream.
   */
  @Nonnull
  @GwtIncompatible
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.FILTERING } )
  public final <DownstreamT extends T> Stream<DownstreamT> ofType( @Nonnull final Class<DownstreamT> type )
  {
    return ofType( null, type );
  }

  /**
   * Remove items in the stream that are not instances of the specified {@code type} and return a stream of the specified type.
   *
   * @param <DownstreamT> the type of item emitted downstream.
   * @param name          the name specified by the user.
   * @param type          the class of items to be emitted downstream.
   * @return the new stream.
   */
  @SuppressWarnings( { "unchecked", "NonJREEmulationClassesInClientCode" } )
  @Nonnull
  @GwtIncompatible
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.FILTERING } )
  public final <DownstreamT extends T> Stream<DownstreamT> ofType( @Nullable final String name,
                                                                   @Nonnull final Class<DownstreamT> type )
  {
    return filter( Spritz.areNamesEnabled() ? generateName( name, "ofType", type.getName() ) : null, type::isInstance )
      .map( i -> (DownstreamT) i );
  }

  /**
   * Drop all items from this stream, only emitting the completion or failed signal.
   *
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> ignoreElements()
  {
    return ignoreElements( null );
  }

  /**
   * Drop all items from this stream, only emitting the completion or failed signal.
   *
   * @param name the name specified by the user.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> ignoreElements( @Nullable final String name )
  {
    return filter( Spritz.areNamesEnabled() ? generateName( name, "ignoreElements" ) : null, e -> false );
  }

  /**
   * Filter the items if they have been previously emitted.
   * To determine whether an item has been previous emitted the {@link Object#equals(Object)}
   * and {@link Object#hashCode()} must be correctly implemented for items type.
   *
   * <p>WARNING: It should be noted that every distinct item is retained until the stream
   * completes. As a result this operator can cause significant amount of memory pressure if many
   * distinct items exist or the stream persists for a long time.</p>
   *
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> distinct()
  {
    return distinct( null );
  }

  /**
   * Filter the items if they have been previously emitted.
   * To determine whether an item has been previous emitted the {@link Object#equals(Object)}
   * and {@link Object#hashCode()} must be correctly implemented for items type.
   *
   * <p>WARNING: It should be noted that every distinct item is retained until the stream
   * completes. As a result this operator can cause significant amount of memory pressure if many
   * distinct items exist or the stream persists for a long time.</p>
   *
   * @param name the name specified by the user.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> distinct( @Nullable final String name )
  {
    return compose( s -> new DistinctOperator<>( name, s ) );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} items in length.
   * If {@code maxSize} is reached then the item will be passed downstream, the downstream will be
   * completed and then the upstream will be cancelled. This method is an alias for {@link #limit(int)}
   *
   * @param maxSize The maximum number of items returned by the stream.
   * @return the new stream.
   * @see #limit(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> take( final int maxSize )
  {
    return limit( maxSize );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} items in length.
   * If {@code maxSize} is reached then the item will be passed downstream, the downstream will be
   * completed and then the upstream will be cancelled. This method is an alias for {@link #take(int)}
   *
   * @param maxSize The maximum number of items returned by the stream.
   * @return the new stream.
   * @see #take(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> limit( final int maxSize )
  {
    return limit( null, maxSize );
  }

  /**
   * Truncate the stream, ensuring the stream is no longer than {@code maxSize} items in length.
   * If {@code maxSize} is reached then the item will be passed downstream, the downstream will be
   * completed and then the upstream will be cancelled. This method is an alias for {@link #take(int)}
   *
   * @param name    the name specified by the user.
   * @param maxSize The maximum number of items returned by the stream.
   * @return the new stream.
   * @see #take(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> limit( @Nullable final String name, final int maxSize )
  {
    return compose( s -> new LimitOperator<>( name, s, maxSize ) );
  }

  /**
   * Pass the first item downstream, complete the downstream and cancel the upstream.
   * This method is an alias for {@link #take(int)} or {@link #limit(int)} where <code>1</code> is
   * passed as the parameter.
   *
   * @return the new stream.
   * @see #take(int)
   * @see #limit(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> first()
  {
    return take( 1 );
  }

  /**
   * Pass the first item downstream, complete the downstream and cancel the upstream.
   * If the stream is empty then signal an error of type {@link NoSuchElementException}.
   *
   * @return the new stream.
   * @see #first()
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> firstOrError()
  {
    return first().errorIfEmpty( NoSuchElementException::new );
  }

  /**
   * Pass the first item downstream, complete the downstream and cancel the upstream.
   * If the stream is empty then emit the defaultValue specified as a parameter.
   *
   * @param defaultValue the public final value emitted if the stream is empty.
   * @return the new stream.
   * @see #first()
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> firstOrDefault( @Nonnull final T defaultValue )
  {
    return first().defaultIfEmpty( defaultValue );
  }

  /**
   * Emit an error if the stream completes and no items were emitted.
   * The error is created by invoking errorFactory when the error will be emitted.
   *
   * @param errorFactory the factory responsible for creating error.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> errorIfEmpty( @Nonnull final Supplier<Throwable> errorFactory )
  {
    return errorIfEmpty( null, errorFactory );
  }

  /**
   * Emit an error if the stream completes and no items were emitted.
   * The error is created by invoking errorFactory when the error will be emitted.
   *
   * @param name         the name specified by the user.
   * @param errorFactory the factory responsible for creating error.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> errorIfEmpty( @Nullable final String name, @Nonnull final Supplier<Throwable> errorFactory )
  {
    return compose( s -> new ErrorIfEmptyOperator<>( name, s, errorFactory ) );
  }

  /**
   * Drop the first {@code count} items of this stream. If the stream contains fewer
   * than {@code count} items then the stream will effectively be an empty stream.
   *
   * @param count the number of items to skip.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> skip( final int count )
  {
    return skip( null, count );
  }

  /**
   * Drop the first {@code count} items of this stream. If the stream contains fewer
   * than {@code count} items then the stream will effectively be an empty stream.
   *
   * @param name  the name specified by the user.
   * @param count the number of items to skip.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> skip( @Nullable final String name, final int count )
  {
    return compose( s -> new SkipOperator<>( name, s, count ) );
  }

  /**
   * Drop all items except for the last item.
   * Once the complete signal has been received the operator will emit the last item received
   * if any prior to emitting the complete signal. This is equivalent to invoking the {@link #last(int)}
   * method and passing the value <code>1</code> to the parameter <code>maxElements</code>.
   *
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> last()
  {
    return last( 1 );
  }

  /**
   * Drop all items except for the last item.
   * If the stream is empty then signal an error of type {@link NoSuchElementException}.
   *
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> lastOrError()
  {
    return last().errorIfEmpty( NoSuchElementException::new );
  }

  /**
   * Drop all items except for the last item.
   * If the stream is empty then emit the defaultValue specified as a parameter.
   *
   * @param defaultValue the public final value emitted if the stream is empty.
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> lastOrDefault( @Nonnull final T defaultValue )
  {
    return last().defaultIfEmpty( defaultValue );
  }

  /**
   * Drop all items except for the last {@code maxElements} items.
   * This operator will buffer up to {@code maxElements} items until it receives the complete
   * signal and then it will emit all the buffered items and the complete signal. If less than
   * {@code maxElements} are emitted by the upstream then it is possible for the downstream to receive
   * less than {@code maxElements} items.
   *
   * @param maxElements the maximum number
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> last( final int maxElements )
  {
    return last( null, maxElements );
  }

  /**
   * Drop all items except for the last {@code maxElements} items.
   * This operator will buffer up to {@code maxElements} items until it receives the complete
   * signal and then it will emit all the buffered items and the complete signal. If less than
   * {@code maxElements} are emitted by the upstream then it is possible for the downstream to receive
   * less than {@code maxElements} items.
   *
   * @param name        the name specified by the user.
   * @param maxElements the maximum number
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> last( @Nullable final String name, final int maxElements )
  {
    return compose( s -> new LastOperator<>( name, s, maxElements ) );
  }

  /**
   * Drop all items except for the last {@code maxElements} items.
   * This operator will buffer up to {@code maxElements} items until it receives the complete
   * signal and then it will emit all the buffered items and the complete signal. If less than
   * {@code maxElements} are emitted by the upstream then it is possible for the downstream to receive
   * less than {@code maxElements} items. This method is an alias for the {@link #last(int)} method.
   *
   * @param maxElements the maximum number
   * @return the new stream.
   * @see #last(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> takeLast( final int maxElements )
  {
    return last( maxElements );
  }

  /**
   * Drop items from this stream until an item no longer matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, no items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns false, all subsequent
   * items will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
   * to {@link #skipUntil(Predicate)} if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the new stream.
   * @see #skipUntil(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> skipWhile( @Nonnull final Predicate<? super T> predicate )
  {
    return skipWhile( null, predicate );
  }

  /**
   * Drop items from this stream until an item no longer matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, no items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns false, all subsequent
   * items will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
   * to {@link #skipUntil(Predicate)} if the predicate is negated.
   *
   * @param name      the name specified by the user.
   * @param predicate The predicate.
   * @return the new stream.
   * @see #skipUntil(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> skipWhile( @Nullable final String name, @Nonnull final Predicate<? super T> predicate )
  {
    return compose( s -> new SkipWhileOperator<>( name, s, predicate ) );
  }

  /**
   * Drop items from this stream until an item matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, no items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns true, all subsequent
   * items will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
   * to {@link #skipWhile(Predicate)} if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the new stream.
   * @see #skipWhile(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> skipUntil( @Nonnull final Predicate<? super T> predicate )
  {
    return skipUntil( null, predicate );
  }

  /**
   * Drop items from this stream until an item matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, no items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns true, all subsequent
   * items will be emitted, and the {@code predicate} will no longer be invoked. This is equivalent
   * to {@link #skipWhile(Predicate)} if the predicate is negated.
   *
   * @param name      the name specified by the user.
   * @param predicate The predicate.
   * @return the new stream.
   * @see #skipWhile(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> skipUntil( @Nullable final String name, @Nonnull final Predicate<? super T> predicate )
  {
    return skipWhile( Spritz.areNamesEnabled() ? generateName( name, "skipUntil" ) : null, predicate.negate() );
  }

  /**
   * Return items from this stream until an item fails to match the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns false, the stream will
   * be completed and the upstream canceled. This is equivalent to {@link #takeUntil(Predicate)}
   * if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the new stream.
   * @see #takeUntil(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> takeWhile( @Nonnull final Predicate<? super T> predicate )
  {
    return takeWhile( null, predicate );
  }

  /**
   * Return items from this stream until an item fails to match the supplied {@code predicate}.
   * As long as the {@code predicate} returns true, items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns false, the stream will
   * be completed and the upstream canceled. This is equivalent to {@link #takeUntil(Predicate)}
   * if the predicate is negated.
   *
   * @param name      the name specified by the user.
   * @param predicate The predicate.
   * @return the new stream.
   * @see #takeUntil(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> takeWhile( @Nullable final String name, @Nonnull final Predicate<? super T> predicate )
  {
    return compose( s -> new TakeWhileOperator<>( name, s, predicate ) );
  }

  /**
   * Return items from this stream until an item matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns true, the stream will
   * be completed and the upstream canceled. This is equivalent to {@link #takeWhile(Predicate)}
   * if the predicate is negated.
   *
   * @param predicate The predicate.
   * @return the new stream.
   * @see #takeWhile(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> takeUntil( @Nonnull final Predicate<? super T> predicate )
  {
    return takeUntil( null, predicate );
  }

  /**
   * Return items from this stream until an item matches the supplied {@code predicate}.
   * As long as the {@code predicate} returns false, items will be emitted from this stream. Once
   * the first item is encountered for which the {@code predicate} returns true, the stream will
   * be completed and the upstream canceled. This is equivalent to {@link #takeWhile(Predicate)}
   * if the predicate is negated.
   *
   * @param name      the name specified by the user.
   * @param predicate The predicate.
   * @return the new stream.
   * @see #takeWhile(Predicate)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SLICING )
  public final Stream<T> takeUntil( @Nullable final String name, @Nonnull final Predicate<? super T> predicate )
  {
    return takeWhile( Spritz.areNamesEnabled() ? generateName( name, "takeUntil" ) : null, predicate.negate() );
  }

  /**
   * Drops items from the stream if they are equal to the previous item emitted by the stream.
   * The items are tested for equality using the {@link Objects#equals(Object, Object)} method.
   * It is equivalent to invoking {@link #filterSuccessive(SuccessivePredicate)} passing a
   * {@link SuccessivePredicate} filters out successive items that are equal.
   *
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> skipRepeats()
  {
    return skipRepeats( null );
  }

  /**
   * Drops items from the stream if they are equal to the previous item emitted by the stream.
   * The items are tested for equality using the {@link Objects#equals(Object, Object)} method.
   * It is equivalent to invoking {@link #filterSuccessive(SuccessivePredicate)} passing a
   * {@link SuccessivePredicate} filters out successive items that are equal.
   *
   * @param name the name specified by the user.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> skipRepeats( @Nullable final String name )
  {
    return filterSuccessive( Spritz.areNamesEnabled() ? generateName( name, "skipRepeats" ) : null,
                             ( prev, current ) -> !Objects.equals( prev, current ) );
  }

  /**
   * Filter consecutive items emitted by this stream using the specified {@link SuccessivePredicate}.
   * Any candidate items that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other items will be skipped. The predicate passes the last emitted item
   * as well as the candidate item.
   *
   * @param predicate the comparator to determine whether two successive items are equal.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> filterSuccessive( @Nonnull final SuccessivePredicate<T> predicate )
  {
    return filterSuccessive( null, predicate );
  }

  /**
   * Filter consecutive items emitted by this stream using the specified {@link SuccessivePredicate}.
   * Any candidate items that return {@code true} when passed to the {@link Predicate} will be
   * emitted while all other items will be skipped. The predicate passes the last emitted item
   * as well as the candidate item.
   *
   * @param name      the name specified by the user.
   * @param predicate the comparator to determine whether two successive items are equal.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.FILTERING )
  public final Stream<T> filterSuccessive( @Nullable final String name,
                                           @Nonnull final SuccessivePredicate<T> predicate )
  {
    return compose( s -> new FilterSuccessiveOperator<>( name, s, predicate ) );
  }

  /**
   * Sample items from stream emitting the first item and the last item in each sample period.
   * If a sampling period ever passes without emitting a value then the sampler is reset and
   * and will start sampling again after the next item is emitted by the upstream stage.
   *
   * @param samplePeriod the period at which the stream is sampled.
   * @return the new stream.
   * @see #sample(int, boolean)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> sample( final int samplePeriod )
  {
    return sample( null, samplePeriod );
  }

  /**
   * Sample items from stream emitting the first item and the last item in each sample period.
   * If a sampling period ever passes without emitting a value then the sampler is reset and
   * and will start sampling again after the next item is emitted by the upstream stage.
   *
   * @param name         the name specified by the user.
   * @param samplePeriod the period at which the stream is sampled.
   * @return the new stream.
   * @see #sample(int, boolean)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> sample( @Nullable final String name, final int samplePeriod )
  {
    return sample( name, samplePeriod, true );
  }

  /**
   * Sample items from stream emitting the last item in each sample period. The first item
   * is emitted if {@code emitInitiatingItem} is <code>true</code>. If a sampling period ever
   * passes without emitting a value then the sampler is reset and and will start sampling
   * again after the next item is emitted by the upstream stage.
   *
   * @param samplePeriod       the period at which the stream is sampled.
   * @param emitInitiatingItem true to emit the first item that initiates sampling.
   * @return the new stream.
   * @see #sample(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> sample( final int samplePeriod, final boolean emitInitiatingItem )
  {
    return sample( null, samplePeriod, emitInitiatingItem );
  }

  /**
   * Sample items from stream emitting the last item in each sample period. The first item
   * is emitted if {@code emitInitiatingItem} is <code>true</code>. If a sampling period ever
   * passes without emitting a value then the sampler is reset and and will start sampling
   * again after the next item is emitted by the upstream stage.
   *
   * @param name               the name specified by the user.
   * @param samplePeriod       the period at which the stream is sampled.
   * @param emitInitiatingItem true to emit the first item that initiates sampling.
   * @return the new stream.
   * @see #sample(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> sample( @Nullable final String name, final int samplePeriod, final boolean emitInitiatingItem )
  {
    return compose( s -> new SampleOperator<>( name, s, samplePeriod, emitInitiatingItem ) );
  }

  /**
   * Drops items emitted by a stream that follow emitted item until the timeout expires.
   *
   * @param timeout the timeout window.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> throttle( final int timeout )
  {
    return throttle( null, timeout );
  }

  /**
   * Drops items emitted by a stream that follow emitted item until the timeout expires.
   *
   * @param name    the name specified by the user.
   * @param timeout the timeout window.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> throttle( @Nullable final String name, final int timeout )
  {
    return throttle( Spritz.areNamesEnabled() ? generateName( name, "throttle", String.valueOf( timeout ) ) : null,
                     i -> timeout );
  }

  /**
   * Drops items emitted by a stream that follow emitted item until the timeout
   * returned by the function expires.
   *
   * @param timeoutForItemFn the function that returns the timeout.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> throttle( @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    return throttle( null, timeoutForItemFn );
  }

  /**
   * Drops items emitted by a stream that follow emitted item until the timeout
   * returned by the function expires.
   *
   * @param name             the name specified by the user.
   * @param timeoutForItemFn the function that returns the timeout.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> throttle( @Nullable final String name, @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    return compose( s -> new ThrottleOperator<>( name, s, timeoutForItemFn ) );
  }

  /**
   * Drops items emitted by a stream that are followed by newer items before
   * the timeout returned by the function expires. The timer resets on each emission.
   *
   * @param timeoutForItemFn the function that returns the timeout.
   * @return the new stream.
   * @see #debounce(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> debounce( @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    return debounce( null, timeoutForItemFn );
  }

  /**
   * Drops items emitted by a stream that are followed by newer items before
   * the timeout returned by the function expires. The timer resets on each emission.
   *
   * @param name             the name specified by the user.
   * @param timeoutForItemFn the function that returns the timeout.
   * @return the new stream.
   * @see #debounce(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> debounce( @Nullable final String name,
                                   @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    return compose( s -> new DebounceOperator<>( name, s, timeoutForItemFn ) );
  }

  /**
   * Drops items emitted by a stream that are followed by newer items before
   * the given timeout value expires. The timer resets on each emission.
   *
   * @param timeout the timeout.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> debounce( final int timeout )
  {
    return debounce( null, timeout );
  }

  /**
   * Drops items emitted by a stream that are followed by newer items before
   * the given timeout value expires. The timer resets on each emission.
   *
   * @param name    the name specified by the user.
   * @param timeout the timeout.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.RATE_LIMITING )
  public final Stream<T> debounce( @Nullable final String name, final int timeout )
  {
    assert timeout > 0;
    return debounce( Spritz.areNamesEnabled() ? generateName( name, "debounce", String.valueOf( timeout ) ) : null,
                     i -> timeout );
  }

  /**
   * Transform items emitted by this stream using the {@code mapper} function.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to use to map the items.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.TRANSFORMATION )
  public final <DownstreamT> Stream<DownstreamT> map( @Nonnull final Function<T, DownstreamT> mapper )
  {
    return map( null, mapper );
  }

  /**
   * Transform items emitted by this stream using the {@code mapper} function.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param name          the name specified by the user.
   * @param mapper        the function to use to map the items.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.TRANSFORMATION )
  public final <DownstreamT> Stream<DownstreamT> map( @Nullable final String name,
                                                      @Nonnull final Function<T, DownstreamT> mapper )
  {
    return compose( s -> new MapOperator<>( name, s, mapper ) );
  }

  /**
   * Transform items emitted by this stream to a constant {@code value}.
   *
   * @param <DownstreamT> the type of the constant value emitted.
   * @param value         the constant value to emit.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.TRANSFORMATION )
  public final <DownstreamT> Stream<DownstreamT> mapTo( final DownstreamT value )
  {
    return mapTo( null, value );
  }

  /**
   * Transform items emitted by this stream to a constant {@code value}.
   *
   * @param <DownstreamT> the type of the constant value emitted.
   * @param name          the name specified by the user.
   * @param value         the constant value to emit.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.TRANSFORMATION )
  public final <DownstreamT> Stream<DownstreamT> mapTo( @Nullable final String name, final DownstreamT value )
  {
    return map( Spritz.areNamesEnabled() ? generateName( name, "mapTo", String.valueOf( value ) ) : null, v -> value );
  }

  /**
   * Map each input item to a stream and then concatenate the items emitted by the mapped stream
   * into this stream. The method operates on a single stream at a time and the result is a concatenation of
   * items emitted from all the streams returned by the mapper function. This method is equivalent to
   * {@link #mergeMap(Function, int)} with a <code>maxConcurrency</code> set to <code>1</code>.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.MERGING } )
  public final <DownstreamT> Stream<DownstreamT> concatMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, 1 );
  }

  /**
   * Map each input item to a stream and then flatten the items emitted by that stream into
   * this stream. The items are merged concurrently up to the maximum concurrency specified by
   * {@code maxConcurrency}. Thus items from different inner streams may be interleaved with other
   * streams that are currently active or subscribed.
   *
   * <p>If an input item is received when the merged stream has already subscribed to the maximum
   * number of inner streams as defined by the <code>maxConcurrency</code> parameter then the extra
   * items are placed on an unbounded buffer. This can lead to significant memory pressure and out
   * of memory conditions if the upstream emits items at a faster rate than the merge stream can
   * complete the inner streams.</p>
   *
   * @param <DownstreamT>  the type of the items that the {@code mapper} function emits.
   * @param mapper         the function to map the items to the inner stream.
   * @param maxConcurrency the maximum number of inner stream that can be subscribed to at one time.
   * @return the new stream.
   * @see #mergeMap(Function)
   */
  @Nonnull
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.MERGING } )
  public final <DownstreamT> Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper,
                                                           final int maxConcurrency )
  {
    return map( mapper ).compose( o -> new MergeOperator<>( null, o, maxConcurrency ) );
  }

  /**
   * Map each input item to a stream and flatten the items emitted by the inner stream into this stream.
   * The number of streams that can be flattened concurrently is specified by {@link #DEFAULT_MERGE_CONCURRENCY}.
   * Invoking this method is equivalent to invoking {@link #mergeMap(Function, int)} and passing the
   * {@link #DEFAULT_MERGE_CONCURRENCY} constant as the {@code maxConcurrency} parameter.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.MERGING } )
  public final <DownstreamT> Stream<DownstreamT> mergeMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, DEFAULT_MERGE_CONCURRENCY );
  }

  /**
   * Map each input item to a stream and emit the items from the most recently
   * mapped stream. The stream that the input item is mapped to is the active stream
   * and all items emitted on the active stream are merged into this stream. If the
   * active stream completes then it is no longer the active stream but this stream does
   * not complete. If a new input item is received while there is an active stream is
   * present then the active stream is canceled and the new input item is mapped to a
   * new stream that is made active.
   *
   * @param <DownstreamT> the type of the items that this stream emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.MERGING } )
  public final <DownstreamT> Stream<DownstreamT> switchMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return map( mapper ).compose( s -> new SwitchOperator<>( null, s ) );
  }

  /**
   * Map each input item to a stream and emit the items from the most recently
   * mapped stream. The stream that the input item is mapped to is the active stream
   * and all items emitted on the active stream are merged into this stream. If the
   * active stream completes then it is no longer the active stream but this stream does
   * not complete. If a new input item is received while there is an active stream is
   * present then the active stream is canceled and the new input item is mapped to a
   * new stream that is made active.
   *
   * @param <DownstreamT> the type of the items that this stream emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.MERGING } )
  public final <DownstreamT> Stream<DownstreamT> exhaustMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return map( mapper ).compose( ExhaustOperator::new );
  }

  /**
   * Concurrently merge items from the specified streams into the current stream. The items from
   * different streams may be interleaved with other streams.
   *
   * @param streams the streams to merge int.
   * @return the new stream.
   * @see #mergeMap(Function)
   */
  @SuppressWarnings( { "unchecked", "varargs", "rawtypes", "RedundantSuppression" } )
  @Nonnull
  @SafeVarargs
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> mergeWith( @Nonnull final Stream<T>... streams )
  {
    final Stream<T>[] upstreams = (Stream<T>[]) new Stream[ streams.length + 1 ];
    upstreams[ 0 ] = this;
    System.arraycopy( streams, 0, upstreams, 1, streams.length );
    return merge( upstreams );
  }

  /**
   * Emit all the items from this stream and then when the complete signal is emitted then
   * merge the items from the specified streams one after another until all streams complete.
   *
   * @param streams the streams to append to this stream.
   * @return the new stream.
   * @see #prepend(Stream[])
   */
  @SuppressWarnings( "varargs" )
  @SafeVarargs
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> append( @Nonnull final Stream<T>... streams )
  {
    final ArrayList<Stream<T>> s = new ArrayList<>( streams.length + 1 );
    s.add( this );
    Collections.addAll( s, streams );
    return compose( p -> fromCollection( s ).compose( o -> new MergeOperator<>( null, o, 1 ) ) );
  }

  /**
   * Merge the items from the specified streams before the items from this stream sequentially.
   * For each of the supplied streams, emit all items from the stream until it completes an then move
   * to the next stream. If no more streams have been supplied then emit the items from this stream.
   *
   * @param streams the stream to prepend to this stream.
   * @return the new stream.
   * @see #prepend(Stream[])
   */
  @SuppressWarnings( "varargs" )
  @SafeVarargs
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> prepend( @Nonnull final Stream<T>... streams )
  {
    final ArrayList<Stream<T>> s = new ArrayList<>( streams.length + 1 );
    Collections.addAll( s, streams );
    s.add( this );
    return compose( p -> fromCollection( s ).compose( o -> new MergeOperator<>( null, o, 1 ) ) );
  }

  /**
   * Emit the specified item before emitting items from this stream.
   *
   * @param value the initial value to emit.
   * @return the new stream.
   * @see #prepend(Stream[])
   */
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> startWith( @Nonnull final T value )
  {
    return prepend( of( value ) );
  }

  /**
   * Emit the specified item after emitting items from this stream.
   *
   * @param value the last value to emit.
   * @return the new stream.
   * @see #append(Stream[])
   */
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> endWith( @Nonnull final T value )
  {
    return append( of( value ) );
  }

  /**
   * Apply an accumulator function to each item in the stream emit the accumulated value.
   *
   * @param <DownstreamT>       the type of the items that the {@code accumulatorFunction} function emits.
   * @param accumulatorFunction the function to use to accumulate the values.
   * @param initialValue        the initial value to begin accumulation from.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ACCUMULATING )
  public final <DownstreamT> Stream<DownstreamT> scan( @Nonnull final AccumulatorFunction<T, DownstreamT> accumulatorFunction,
                                                       @Nonnull final DownstreamT initialValue )
  {
    return scan( null, accumulatorFunction, initialValue );
  }

  /**
   * Apply an accumulator function to each item in the stream emit the accumulated value.
   *
   * @param <DownstreamT>       the type of the items that the {@code accumulatorFunction} function emits.
   * @param name                the name specified by the user.
   * @param accumulatorFunction the function to use to accumulate the values.
   * @param initialValue        the initial value to begin accumulation from.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ACCUMULATING )
  public final <DownstreamT> Stream<DownstreamT> scan( @Nullable final String name,
                                                       @Nonnull final AccumulatorFunction<T, DownstreamT> accumulatorFunction,
                                                       @Nonnull final DownstreamT initialValue )
  {
    return compose( s -> new ScanOperator<>( name, s, accumulatorFunction, initialValue ) );
  }

  /**
   * Invoke the {@link Subscriber#onSubscribe(Subscription)} on upstream on the specified {@link VirtualProcessorUnit}.
   *
   * @param virtualProcessorUnit the VPU on which to invoke onSubscribe.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SCHEDULING )
  public final Stream<T> subscribeOn( @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    return subscribeOn( null, virtualProcessorUnit );
  }

  /**
   * Invoke the {@link Subscriber#onSubscribe(Subscription)} on upstream on the specified {@link VirtualProcessorUnit}.
   *
   * @param name                 the name specified by the user.
   * @param virtualProcessorUnit the VPU on which to invoke onSubscribe.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SCHEDULING )
  public final Stream<T> subscribeOn( @Nullable final String name,
                                      @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    return compose( s -> new SubscribeOnOperator<>( name, s, virtualProcessorUnit ) );
  }

  /**
   * Emit signals and item on the specified {@link VirtualProcessorUnit}.
   * In practical terms this means that all of the {@link Subscription} methods for
   * the downstream are invoked on the specified {@link VirtualProcessorUnit}.
   *
   * @param virtualProcessorUnit the VPU on which to invoke signals and emit items.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SCHEDULING )
  public final Stream<T> observeOn( @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    return observeOn( null, virtualProcessorUnit );
  }

  /**
   * Emit signals and item on the specified {@link VirtualProcessorUnit}.
   * In practical terms this means that all of the {@link Subscription} methods for
   * the downstream are invoked on the specified {@link VirtualProcessorUnit}.
   *
   * @param name                 the name specified by the user.
   * @param virtualProcessorUnit the VPU on which to invoke signals and emit items.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.SCHEDULING )
  public final Stream<T> observeOn( @Nullable final String name,
                                    @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    return compose( s -> new ObserveOnOperator<>( name, s, virtualProcessorUnit ) );
  }

  /**
   * When an upstream emits an error then replace upstream with the stream returned by the supplied function rather
   * than emitting an error to downstream. If the function throws an exception or returns null then the original
   * error will be emitted downstream.
   *
   * @param streamFromErrorFn the function invoked when upstream emits an error.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ERROR_HANDLING )
  public final Stream<T> rescue( @Nonnull final Function<Throwable, Stream<T>> streamFromErrorFn )
  {
    return rescue( null, streamFromErrorFn );
  }

  /**
   * When an upstream emits an error then replace upstream with the stream returned by the supplied function rather
   * than emitting an error to downstream. If the function throws an exception or returns null then the original
   * error will be emitted downstream.
   *
   * @param name              the name specified by the user.
   * @param streamFromErrorFn the function invoked when upstream emits an error.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ERROR_HANDLING )
  public final Stream<T> rescue( @Nullable final String name,
                                 @Nonnull final Function<Throwable, Stream<T>> streamFromErrorFn )
  {
    return compose( s -> new OnErrorResumeWithOperator<>( name, s, streamFromErrorFn ) );
  }

  /**
   * When an upstream emits an error then emit supplied value and complete the stream.
   *
   * @param value the value to emit on error.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ERROR_HANDLING )
  public final Stream<T> rescueThenReturn( @Nonnull final T value )
  {
    return rescueThenReturn( null, value );
  }

  /**
   * When an upstream emits an error then emit supplied value and complete the stream.
   *
   * @param name  the name specified by the user.
   * @param value the value to emit on error.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ERROR_HANDLING )
  public final Stream<T> rescueThenReturn( @Nullable final String name, @Nonnull final T value )
  {
    final String actualName =
      Spritz.areNamesEnabled() ? generateName( name, "onErrorReturn", String.valueOf( value ) ) : null;
    return rescue( actualName, e -> of( value ) );
  }

  /**
   * When an upstream emits an error then re-subscribe to upstream rather than emitting an error to downstream.
   * This recovery process will occur up to {@code maxErrorCount} times.
   *
   * @param maxErrorCount the maximum number of times to try and re-susbcribe to upstream.
   * @return the new stream.
   * @see #repeat()
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ERROR_HANDLING )
  public final Stream<T> repeat( final int maxErrorCount )
  {
    return repeat( null, maxErrorCount );
  }

  /**
   * When an upstream emits an error then re-subscribe to upstream rather than emitting an error to downstream.
   * This recovery process will occur up to {@code maxErrorCount} times.
   *
   * @param name          the name specified by the user.
   * @param maxErrorCount the maximum number of times to try and re-susbcribe to upstream.
   * @return the new stream.
   * @see #repeat()
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ERROR_HANDLING )
  public final Stream<T> repeat( @Nullable final String name, final int maxErrorCount )
  {
    final int[] state = new int[]{ maxErrorCount };
    final String actualName =
      Spritz.areNamesEnabled() ? generateName( name, "repeat", String.valueOf( maxErrorCount ) ) : null;
    return rescue( actualName, e -> ( --state[ 0 ] ) >= 0 ? this : null );
  }

  /**
   * When an upstream emits an error then re-subscribe to upstream rather than emitting an error to downstream.
   *
   * @return the new stream.
   * @see #repeat(int)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.ERROR_HANDLING )
  public final Stream<T> repeat()
  {
    return repeat( Integer.MAX_VALUE );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The new stream emits to downstream {@link Subscriber}s only those items that are emitted subsequent
   * to the time that the downstream {@link Subscriber} subscribes. The new stream subscribes to this stream
   * when the {@link ConnectableStream#connect()} method is called.
   *
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publish()
  {
    return publish( null );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The new stream emits to downstream {@link Subscriber}s only those items that are emitted subsequent
   * to the time that the downstream {@link Subscriber} subscribes. The new stream subscribes to this stream
   * when the {@link ConnectableStream#connect()} method is called.
   *
   * @param name the name specified by the user.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publish( @Nullable final String name )
  {
    return multicast( new Subject<>( Spritz.areNamesEnabled() ? generateName( name, "publish" ) : null ) );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The created stream emits the current value to downstream {@link Subscriber}s and then emits items
   * any items that are emitted subsequent to the time that the downstream {@link Subscriber} subscribes.
   * The current value is the last emitted value or the initialValue if no value has been emitted by the upstream.
   * The new stream subscribes to this stream when the {@link ConnectableStream#connect()} method is called.
   *
   * @param initialValue the initial value.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publishCurrentValue( @Nonnull final T initialValue )
  {
    return publishCurrentValue( null, initialValue );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The created stream emits the current value to downstream {@link Subscriber}s and then emits items
   * any items that are emitted subsequent to the time that the downstream {@link Subscriber} subscribes.
   * The current value is the last emitted value or the initialValue if no value has been emitted by the upstream.
   * The new stream subscribes to this stream when the {@link ConnectableStream#connect()} method is called.
   *
   * @param name         the name specified by the user.
   * @param initialValue the initial value.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publishCurrentValue( @Nullable final String name, @Nonnull final T initialValue )
  {
    final String actualName = generateName( name, "publishCurrentValue", String.valueOf( initialValue ) );
    return multicast( new CurrentValueSubject<>( Spritz.areNamesEnabled() ? actualName : null, initialValue ) );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The created stream buffers and replays events to new {@link Subscriber}s and then emits
   * events to the new subscriber as they are received. The buffering is not unbound and the developer
   * should be careful to avoid excessive memory pressure.
   *
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publishReplay()
  {
    return publishReplay( ReplaySubject.DEFAULT_VALUE, ReplaySubject.DEFAULT_VALUE );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The created stream buffers and replays events to new {@link Subscriber}s and then emits
   * events to the new subscriber as they are received. The buffering is bound by size.
   *
   * @param maxSize the maximum number of items to replay.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publishReplayWithMaxSize( final int maxSize )
  {
    return publishReplay( maxSize, ReplaySubject.DEFAULT_VALUE );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The created stream buffers and replays events to new {@link Subscriber}s and then emits
   * events to the new subscriber as they are received. The buffering is bound by age.
   *
   * @param maxAge the oldest age of items to replay.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publishReplayWithMaxAge( final int maxAge )
  {
    return publishReplay( ReplaySubject.DEFAULT_VALUE, maxAge );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The created stream buffers and replays events to new {@link Subscriber}s and then emits
   * events to the new subscriber as they are received. The buffering is bound by size and age.
   *
   * @param maxSize the maximum number of items to replay.
   * @param maxAge  the oldest age of items to replay.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publishReplay( final int maxSize, final int maxAge )
  {
    return publishReplay( null, maxSize, maxAge );
  }

  /**
   * Create a multicast, {@link ConnectableStream} that shares a single subscription to this stream.
   * The created stream buffers and replays events to new {@link Subscriber}s and then emits
   * events to the new subscriber as they are received. The buffering is bound by size and age.
   *
   * @param name    the name specified by the user.
   * @param maxSize the maximum number of items to replay.
   * @param maxAge  the oldest age of items to replay.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final ConnectableStream<T> publishReplay( @Nullable final String name, final int maxSize, final int maxAge )
  {
    final String actualName =
      Spritz.areNamesEnabled() ?
      generateName( name,
                    "publishReplay",
                    ( ReplaySubject.DEFAULT_VALUE == maxSize ? "unbound" : String.valueOf( maxSize ) ) + "," +
                    ( ReplaySubject.DEFAULT_VALUE == maxAge ? "unbound" : String.valueOf( maxAge ) ) ) :
      null;
    return multicast( new ReplaySubject<>( actualName, maxSize, maxAge ) );
  }

  /**
   * Create a stream that multicasts the source Stream. As long as there is at least one Subscriber
   * this stream will be subscribed to the source Stream and emitting data. When all subscribers have
   * unsubscribed it will unsubscribe from the source Stream.
   *
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> share()
  {
    return share( null );
  }

  /**
   * Create a stream that multicasts the source Stream. As long as there is at least one Subscriber
   * this stream will be subscribed to the source Stream and emitting data. When all subscribers have
   * unsubscribed it will unsubscribe from the source Stream.
   *
   * @param name the name specified by the user.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> share( @Nullable final String name )
  {
    return publish().refCount();
  }

  /**
   * Publish emitted items and signals to the specified subject.
   * When downstream stages subscribe they are subscribed to the subject.
   * This stage only subscribes to upstream when the {@link ConnectableStream#connect()} method is called on the
   * returned stream.
   *
   * @param subject the subject to publish to.
   * @return the new stream.
   */
  @Nonnull
  private ConnectableStream<T> multicast( @Nonnull final Subject<T> subject )
  {
    return compose( s -> new ConnectableStream<>( Spritz.areNamesEnabled() ? "multicast" : null, s, subject ) );
  }

  /**
   * If upstream emits no items and then completes then emit the {@code defaultValue} before completing this stream.
   *
   * @param defaultValue the public final value to emit if upstream completes and is empty.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> defaultIfEmpty( @Nonnull final T defaultValue )
  {
    return defaultIfEmpty( null, defaultValue );
  }

  /**
   * If upstream emits no items and then completes then emit the {@code defaultValue} before completing this stream.
   *
   * @param name         the name specified by the user.
   * @param defaultValue the public final value to emit if upstream completes and is empty.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> defaultIfEmpty( @Nullable final String name, @Nonnull final T defaultValue )
  {
    return compose( s -> new DefaultIfEmptyOperator<>( name, s, defaultValue ) );
  }

  /**
   * Signals error with a {@link TimeoutException} if an item is not emitted within the specified timeout period from the previous item.
   *
   * @param timeoutTime the timeout period after which the stream is terminated.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> timeout( final int timeoutTime )
  {
    return timeout( null, timeoutTime );
  }

  /**
   * Signals error with a {@link TimeoutException} if an item is not emitted within the specified timeout period from the previous item.
   *
   * @param name        the name specified by the user.
   * @param timeoutTime the timeout period after which the stream is terminated.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> timeout( @Nullable final String name, final int timeoutTime )
  {
    return compose( s -> new TimeoutOperator<>( name, s, timeoutTime ) );
  }

  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Subscription forEach( @Nonnull final Consumer<T> action )
  {
    return subscribe( new ForEachSubscriber<>( action ) );
  }

  /**
   * Compost this stream with another stream and return the new stream.
   * This method is used to compose chains of stream operations.
   *
   * @param <DownstreamT> the type of the item emitted by downstream stage.
   * @param <S>           the type of the downstream stage.
   * @param function      the function used to compose stream operations.
   * @return the new stream.
   */
  @Nonnull
  public final <DownstreamT, S extends Stream<DownstreamT>> S compose( @Nonnull final Function<Stream<T>, S> function )
  {
    return function.apply( this );
  }

  /**
   * Return the local name of the stream.
   * This method should NOT be invoked unless {@link Spritz#areNamesEnabled()} returns <code>true</code>.
   *
   * @return the local name of the node.
   */
  @Nonnull
  final String getName()
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( Spritz::areNamesEnabled,
                    () -> "Spritz-0053: Stream.getName() invoked when Spritz.areNamesEnabled() is false" );
    }
    assert null != _name;
    return _name;
  }

  /**
   * Return the qualified name of the stream.
   * The qualified name includes the local name suffixed to the upstream name.
   * This method should NOT be invoked unless {@link Spritz#areNamesEnabled()} returns <code>true</code>.
   *
   * @return the qualified name of the node.
   */
  @Nonnull
  String getQualifiedName()
  {
    return getName();
  }

  @Nonnull
  @Override
  public final String toString()
  {
    if ( Spritz.areNamesEnabled() )
    {
      return getQualifiedName();
    }
    else
    {
      return super.toString();
    }
  }

  /**
   * Build name for Stream.
   * If {@link Spritz#areNamesEnabled()} returns false then this method will return null, otherwise the specified
   * name will be returned or a name synthesized from the prefix if no name is
   * specified.
   *
   * @param name   the name specified by the user.
   * @param prefix the prefix used if this method needs to generate name.
   * @return the name.
   */
  @Nullable
  static String generateName( @Nullable final String name, @Nonnull final String prefix )
  {
    return generateName( name, prefix, null );
  }

  /**
   * Build name for Stream.
   * If {@link Spritz#areNamesEnabled()} returns false then this method will return null, otherwise the specified
   * name will be returned or a name synthesized from the prefix and params if no name is
   * specified.
   *
   * @param name   the name specified by the user.
   * @param prefix the prefix used if this method needs to generate name.
   * @param params a description of parameters used constructing the stream if any.
   * @return the name.
   */
  @Nullable
  static String generateName( @Nullable final String name, @Nonnull final String prefix, @Nullable final String params )
  {
    return Spritz.areNamesEnabled() ?
           null != name ? name : prefix + "(" + ( null == params ? "" : params ) + ")" :
           null;
  }
}
