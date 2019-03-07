package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract subscription implementation.
 */
abstract class AbstractSubscription<T>
  implements Subscription
{
  /**
   * The subscriber associated with the subscription.
   */
  @Nonnull
  private final Subscriber<? super T> _subscriber;
  private boolean _done;

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

  final boolean isNotDone()
  {
    return !isDone();
  }

  void doCancel()
  {
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public final String toString()
  {
    if ( Spritz.areNamesEnabled() )
    {
      return "Subscription[" + getQualifiedName() + "]";
    }
    else
    {
      return super.toString();
    }
  }

  abstract String getQualifiedName();
}
