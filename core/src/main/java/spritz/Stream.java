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
import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.GwtIncompatible;
import spritz.internal.annotations.MetaDataSource;

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
   * Creates a stream that emits the parameters as items and then emits the completion signal.
   *
   * @param <T>    the type of items contained in the stream.
   * @param values the values to emit.
   * @return the new stream.
   */
  @SafeVarargs
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static <T> Stream<T> of( @Nonnull final T... values )
  {
    return new StaticStreamSource<>( values );
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
    return of();
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
    return new FailStreamSource<>( error );
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
    return new CollectionStreamSource<>( values );
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
    return new GenerateStreamSource<>( callable );
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
    return new GenerateStreamSource<>( supplier::get );
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
    return new RunnableStreamSource<>( runnable );
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
    return periodic( period ).map( e -> supplier.get() );
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
  @DocCategory( DocCategory.Type.CONSTRUCTION )
  public static Stream<Integer> range( final int start, final int count )
  {
    return new RangeStreamSource( start, count );
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
    return new PeriodicStreamSource( period );
  }

  @DocCategory( DocCategory.Type.CONSTRUCTION )
  @SafeVarargs
  public static <T> Stream<T> concat( @Nonnull final Stream<T>... upstreams )
  {
    return of( upstreams ).concatMap( v -> v );
  }

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
    return new CreateStreamSource<>( createFunction );
  }

  /**
   * {@inheritDoc}
   */
  public final void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    doSubscribe( Spritz.shouldValidateSubscriptions() ? new ValidatingSubscriber<>( subscriber ) : subscriber );
  }

  protected abstract void doSubscribe( @Nonnull Subscriber<? super T> subscriber );

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
  public final Stream<T> onSubscribe( @Nonnull final Consumer<Subscription> action )
  {
    return new PeekOperator<>( this, action, null, null, null, null, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling subscription.
   *
   * @param action the function called after signalling subscription.
   * @return the new stream.
   * @see #onSubscribe(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterSubscribe( @Nonnull final Consumer<Subscription> action )
  {
    return new PeekOperator<>( this, null, action, null, null, null, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #onNext(Consumer)}.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #onNext(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> tap( @Nonnull final Consumer<? super T> action )
  {
    return onNext( action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #onNext(Consumer)}.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #onNext(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> peek( @Nonnull final Consumer<? super T> action )
  {
    return onNext( action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #peek(Consumer)}.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #peek(Consumer)
   * @see #tap(Consumer)
   * @see #afterNext(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> onNext( @Nonnull final Consumer<? super T> action )
  {
    return new PeekOperator<>( this, null, null, action, null, null, null, null, null, null, null );
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
    return new PeekOperator<>( this, null, null, null, action, null, null, null, null, null, null );
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
  public final Stream<T> onError( @Nonnull final Consumer<Throwable> action )
  {
    return new PeekOperator<>( this, null, null, null, null, action, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling error.
   *
   * @param action the function called after signalling error.
   * @return the new stream.
   * @see #onError(Consumer)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterError( @Nonnull final Consumer<Throwable> action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, action, null, null, null, null );
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
  public final Stream<T> onComplete( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, action, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete.
   *
   * @param action the function called when the stream completes.
   * @return the new stream.
   * @see #onComplete(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterComplete( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, null, action, null, null );
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
  public final Stream<T> onCancel( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, null, null, action, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after the stream is canceled by a downstream stage.
   *
   * @param action the function called after the stream is canceled by a downstream stage.
   * @return the new stream.
   * @see #onCancel(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterCancel( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, null, null, null, null, action );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #onError(Consumer)} and
   * {@link #onComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param action the function called before signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #afterTerminate(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> onTerminate( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, e -> action.run(), null, action, null, action, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #onError(Consumer)} and
   * {@link #onComplete(Runnable)}. In addition, the action is called if the stream is
   * cancelled by a downstream stage.
   *
   * @param action the function called after signalling complete or signalling error or being cancelled by downstream stage.
   * @return the new stream.
   * @see #onTerminate(Runnable)
   */
  @Nonnull
  @DocCategory( DocCategory.Type.PEEKING )
  public final Stream<T> afterTerminate( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( this, null, null, null, null, null, e -> action.run(), null, action, null, action );
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
    return compose( p -> new PredicateFilterStream<>( p, predicate ) );
  }

  /**
   * Remove items in the stream that are not instances of the specified {@code type} and return a stream of the specified type.
   *
   * @param <DownstreamT> the type of item emitted downstream.
   * @param type          the class of items to be emitted downstream.
   * @return the new stream.
   */
  @SuppressWarnings( "unchecked" )
  @Nonnull
  @GwtIncompatible
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.FILTERING } )
  public final <DownstreamT extends T> Stream<DownstreamT> ofType( @Nonnull final Class<DownstreamT> type )
  {
    return filter( type::isInstance ).map( i -> (DownstreamT) i );
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
    return filter( e -> false );
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
    return compose( DistinctOperator::new );
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
    return compose( p -> new LimitOperator<>( p, maxSize ) );
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
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> errorIfEmpty( @Nonnull final Supplier<Throwable> errorFactory )
  {
    return compose( p -> new ErrorIfEmptyOperator<>( p, errorFactory ) );
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
    return compose( p -> new SkipOperator<>( p, count ) );
  }

  /**
   * Drop all items except for the last item.
   * Once the complete signal has been received the operator will emit the last item received
   * if any prior to sending the complete signal. This is equivalent to invoking the {@link #last(int)}
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
   * signal and then it will send all the buffered items and the complete signal. If less than
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
    return compose( p -> new LastOperator<>( p, maxElements ) );
  }

  /**
   * Drop all items except for the last {@code maxElements} items.
   * This operator will buffer up to {@code maxElements} items until it receives the complete
   * signal and then it will send all the buffered items and the complete signal. If less than
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
    return compose( p -> new SkipWhileOperator<>( p, predicate ) );
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
    return skipWhile( predicate.negate() );
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
    return compose( p -> new TakeWhileOperator<>( p, predicate ) );
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
    return takeWhile( predicate.negate() );
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
    return filterSuccessive( ( prev, current ) -> !Objects.equals( prev, current ) );
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
    return compose( s -> new FilterSuccessiveOperator<>( s, predicate ) );
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
    return sample( samplePeriod, true );
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
    return compose( s -> new SampleOperator<>( s, samplePeriod, emitInitiatingItem ) );
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
    return throttle( i -> timeout );
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
    return compose( s -> new ThrottleOperator<>( s, timeoutForItemFn ) );
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
    return compose( s -> new DebounceOperator<>( s, timeoutForItemFn ) );
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
    assert timeout > 0;
    return debounce( i -> timeout );
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
    return compose( p -> new MapOperator<>( p, mapper ) );
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
    return map( v -> value );
  }

  /**
   * Map each input item to a stream and then concatenate the items emitted by the mapped stream
   * into this stream. The method operates on a single stream at a time and the result is a concatenation of
   * items emitted from all the streams returned by the mapper function. This method is equivalent to
   * {@link #mergeMap(Function, int)} with a <code>maxConcurrency</code> set to <code>1</code>. This
   * method is also an alias for {@link #concatMap(Function)}.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   * @see #concatMap(Function)
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.MERGING } )
  public final <DownstreamT> Stream<DownstreamT> flatMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return mergeMap( mapper, 1 );
  }

  /**
   * Map each input item to a stream and then concatenate the items emitted by the mapped stream
   * into this stream. The method operates on a single stream at a time and the result is a concatenation of
   * items emitted from all the streams returned by the mapper function. This method is equivalent to
   * {@link #mergeMap(Function, int)} with a <code>maxConcurrency</code> set to <code>1</code>. This
   * method is also an alias for {@link #flatMap(Function)}.
   *
   * @param <DownstreamT> the type of the items that the {@code mapper} function emits.
   * @param mapper        the function to map the items to the inner stream.
   * @return the new stream.
   * @see #flatMap(Function)
   * @see #mergeMap(Function, int)
   */
  @Nonnull
  @DocCategory( { DocCategory.Type.TRANSFORMATION, DocCategory.Type.MERGING } )
  public final <DownstreamT> Stream<DownstreamT> concatMap( @Nonnull final Function<T, Stream<DownstreamT>> mapper )
  {
    return flatMap( mapper );
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
    return compose( p -> new MapOperator<>( p, mapper ).compose( o -> new MergeOperator<>( o, maxConcurrency ) ) );
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
    return compose( p -> new MapOperator<>( p, mapper ).compose( SwitchOperator::new ) );
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
    return compose( p -> new MapOperator<>( p, mapper ).compose( ExhaustOperator::new ) );
  }

  /**
   * Emit all the items from this stream and then when the complete signal is emitted then
   * merge the items from the specified streams one after another until all streams complete.
   *
   * @param streams the streams to append to this stream.
   * @return the new stream.
   * @see #prepend(Stream[])
   */
  @SafeVarargs
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> append( @Nonnull final Stream<T>... streams )
  {
    final ArrayList<Stream<T>> s = new ArrayList<>( streams.length + 1 );
    s.add( this );
    Collections.addAll( s, streams );
    return compose( p -> fromCollection( s ).compose( o -> new MergeOperator<>( o, 1 ) ) );
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
  @SafeVarargs
  @Nonnull
  @DocCategory( DocCategory.Type.MERGING )
  public final Stream<T> prepend( @Nonnull final Stream<T>... streams )
  {
    final ArrayList<Stream<T>> s = new ArrayList<>( streams.length + 1 );
    Collections.addAll( s, streams );
    s.add( this );
    return compose( p -> fromCollection( s ).compose( o -> new MergeOperator<>( o, 1 ) ) );
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
    return compose( p -> new ScanOperator<>( p, accumulatorFunction, initialValue ) );
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
    return compose( p -> new SubscribeOnOperator<>( p, virtualProcessorUnit ) );
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
    return compose( p -> new ObserveOnOperator<>( p, virtualProcessorUnit ) );
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
  public final Stream<T> onErrorResumeWith( @Nonnull final Function<Throwable, Stream<T>> streamFromErrorFn )
  {
    return compose( p -> new OnErrorResumeWithOperator<>( p, streamFromErrorFn ) );
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
    final int[] state = new int[]{ maxErrorCount };
    return onErrorResumeWith( e -> ( --state[ 0 ] ) >= 0 ? this : null );
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
   * If upstream emits no items and then completes then emit the {@code defaultValue} before completing this stream.
   *
   * @param defaultValue the public final value to emit if upstream completes and is empty.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> defaultIfEmpty( @Nonnull final T defaultValue )
  {
    return compose( p -> new DefaultIfEmptyOperator<>( p, defaultValue ) );
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
    return compose( p -> new TimeoutOperator<>( p, timeoutTime ) );
  }

  @DocCategory( DocCategory.Type.UNKNOWN )
  public final void forEach( @Nonnull final Consumer<T> action )
  {
    terminate( () -> new ForEachSubscriber<>( action ) );
  }

  @DocCategory( DocCategory.Type.UNKNOWN )
  public final void terminate( @Nonnull final Supplier<Subscriber<T>> terminateFunction )
  {
    subscribe( terminateFunction.get() );
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
}
