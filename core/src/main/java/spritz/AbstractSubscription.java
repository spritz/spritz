package spritz;

/**
 * Abstract subscription implementation for the common scenario where
 * there is an upstream stage and associated subscription.
 */
abstract class AbstractSubscription
  implements Subscription
{
  private boolean _done;

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
