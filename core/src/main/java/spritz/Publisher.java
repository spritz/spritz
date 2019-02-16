package spritz;

import javax.annotation.Nonnull;

/**
 * A subscriber can subscribe to receive events from a publisher.
 * This interface is an abstract interface that can be used to represent both streams and subjects.
 */
public interface Publisher<T>
{
  /**
   * Subscribe the subscriber to this publisher so that it can receive events.
   *
   * @param subscriber the subscriber.
   */
  void subscribe( @Nonnull Subscriber<? super T> subscriber );
}
