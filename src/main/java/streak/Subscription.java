package streak;

public interface Subscription
{
  /**
   * Cancel subscription.
   * After this method is invoked the subscription will emit no items or signals to downstream stages
   * and will release resources associated with the subscription which may include upstream subscriptions.
   */
  void cancel();
}
