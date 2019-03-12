package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract subscription implementation.
 */
abstract class AbstractSubscription<T>
  extends Subscription
{
  /**
   * The subscriber associated with the subscription.
   */
  @Nonnull
  private final Subscriber<? super T> _subscriber;

  AbstractSubscription( @Nonnull final Subscriber<? super T> subscriber )
  {
    _subscriber = Objects.requireNonNull( subscriber );
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
}
