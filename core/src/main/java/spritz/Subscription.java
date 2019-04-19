package spritz;

import javax.annotation.Nonnull;

public abstract class Subscription
{
  /**
   * Flag indicating that the subscription has either been cancelled or has finalized via {@link Subscriber#onComplete()}
   * or {@link Subscriber#onError(Throwable)}.
   */
  private boolean _done;

  /**
   * Cancel subscription.
   * After this method is invoked the subscription will emit no items or signals to downstream stages
   * and will release resources associated with the subscription which may include upstream subscriptions.
   */
  public final void cancel()
  {
    if ( isNotDone() )
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
