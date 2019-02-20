package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract subscription implementation for the common scenario where
 * there is an upstream stage and associated subscription.
 */
abstract class AbstractSubscription<T, S extends Stream<T>>
  implements Subscription
{
  /**
   * The stream from which this subscription was created.
   */
  @Nonnull
  private final S _stream;
  /**
   * The subscriber associated with the subscription.
   */
  @Nonnull
  private final Subscriber<? super T> _subscriber;
  private boolean _done;

  AbstractSubscription( @Nonnull final S stream, @Nonnull final Subscriber<? super T> subscriber )
  {
    _stream = Objects.requireNonNull( stream );
    _subscriber = Objects.requireNonNull( subscriber );
  }

  @Nonnull
  final S getStream()
  {
    return _stream;
  }

  /**
   * Return the subscriber.
   *
   * @return the subscriber.
   */
  @Nonnull
  final Subscriber<? super T> getSubscriber()
  {
    return _subscriber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void cancel()
  {
    if ( !_done )
    {
      markAsDone();
      doCancel();
    }
  }

  final void markAsDone()
  {
    _done = true;
  }

  final boolean isDone()
  {
    return _done;
  }

  void doCancel()
  {
  }
}
