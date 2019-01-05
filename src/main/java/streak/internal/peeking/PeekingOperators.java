package streak.internal.peeking;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.StreamExtension;

/**
 * Operators for inspecting items as they pass through the stream.
 *
 * @param <T> the type of the items that the stream consumes and emits.
 */
public interface PeekingOperators<T>
  extends StreamExtension<T>
{
  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before each item is emitted. This method is an alias for {@link #onNext(Consumer)}.
   *
   * @param action the function before each item is emitted.
   * @return the new stream.
   * @see #onNext(Consumer)
   */
  @Nonnull
  default Flow.Stream<T> peek( @Nonnull final Consumer<? super T> action )
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
   * @see #afterNext(Consumer)
   */
  @Nonnull
  default Flow.Stream<T> onNext( @Nonnull final Consumer<? super T> action )
  {
    return new PeekOperator<>( self(), action, null, null, null, null, null, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after each item is emitted. This method is an alias for {@link #peek(Consumer)}.
   *
   * @param action the function after each item is emitted.
   * @return the new stream.
   */
  @Nonnull
  default Flow.Stream<T> afterNext( @Nonnull final Consumer<? super T> action )
  {
    return new PeekOperator<>( self(), null, action, null, null, null, null, null, null );
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
  default Flow.Stream<T> onError( @Nonnull final Consumer<Throwable> action )
  {
    return new PeekOperator<>( self(), null, null, action, null, null, null, null, null );
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
  default Flow.Stream<T> afterError( @Nonnull final Consumer<Throwable> action )
  {
    return new PeekOperator<>( self(), null, null, null, action, null, null, null, null );
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
  default Flow.Stream<T> onComplete( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( self(), null, null, null, null, action, null, null, null );
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
  default Flow.Stream<T> afterComplete( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( self(), null, null, null, null, null, action, null, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter before signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #onError(Consumer)} and
   * {@link #onComplete(Runnable)}. In addition, the action is called if the stream is
   * disposed by a downstream stage.
   *
   * @param action the function called before signalling complete or signalling error or being disposed by downstream stage.
   * @return the new stream.
   * @see #afterTerminate(Runnable)
   */
  @Nonnull
  default Flow.Stream<T> onTerminate( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( self(), null, null, e -> action.run(), null, action, null, action, null );
  }

  /**
   * Return a stream containing all the items from this stream that invokes the action
   * parameter after signalling complete or signalling error. If you need to know know
   * whether the stream failed or completed then use {@link #onError(Consumer)} and
   * {@link #onComplete(Runnable)}. In addition, the action is called if the stream is
   * disposed by a downstream stage.
   *
   * @param action the function called after signalling complete or signalling error or being disposed by downstream stage.
   * @return the new stream.
   * @see #onTerminate(Runnable)
   */
  @Nonnull
  default Flow.Stream<T> afterTerminate( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( self(), null, null, null, e -> action.run(), null, action, null, action );
  }
}
