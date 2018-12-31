package streak.internal.peeking;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.StreamExtension;

/**
 * Operators for inspecting elements as they pass through stream.
 *
 * @param <T> the type of the elements that the stream consumes and emits.
 */
public interface PeekingOperators<T>
  extends StreamExtension<T>
{
  /**
   * Return a stream containing all the elements from this stream that invokes the provided
   * action for each element.
   *
   * @param action the function called for every element.
   * @return the new stream.
   */
  @Nonnull
  default Flow.Stream<T> peek( @Nonnull final Consumer<? super T> action )
  {
    return new PeekOperator<>( self(), action, null, null, null );
  }

  /**
   * Return a stream containing all the elements from this stream that invokes the provided
   * action when the stream fails.
   *
   * @param action the function called with the failure.
   * @return the new stream.
   */
  @Nonnull
  default Flow.Stream<T> onError( @Nonnull final Consumer<Throwable> action )
  {
    return new PeekOperator<>( self(), null, action, null, null );
  }

  /**
   * Return a stream containing all the elements from this stream that invokes the provided
   * action when the stream completes.
   *
   * @param action the function called when the stream completes.
   * @return the new stream.
   */
  @Nonnull
  default Flow.Stream<T> onComplete( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( self(), null, null, action, null );
  }

  /**
   * Return a stream containing all the elements from this stream that performs the provided
   * action when the stream fails or completes. If you need to know know whether the stream failed
   * or completed then use {@link #onError(Consumer)} and {@link #onComplete(Runnable)}. In addition,
   * the action is called if the stream is disposed by a downstream stage.
   *
   * @param action the function called when the stream completes or failed.
   * @return the new stream.
   */
  @Nonnull
  default Flow.Stream<T> onTerminate( @Nonnull final Runnable action )
  {
    return new PeekOperator<>( self(), null, e -> action.run(), action, action );
  }
}
