package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A subscriber that forwards events onto downstream subscribers form a hub.
 */
final class ForwardToHubDownstreamSubscriber<T>
  extends Subscription
  implements Subscriber<T>
{
  @Nonnull
  private final Hub<?, T> _hub;
  @Nullable
  private Subscription _subscription;

  ForwardToHubDownstreamSubscriber( @Nonnull final Hub<?, T> hub )
  {
    _hub = Objects.requireNonNull( hub );
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    _subscription = subscription;
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    _hub.downstreamNext( item );
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    markAsDone();
    _hub.downstreamError( error );
    _subscription = null;
  }

  @Override
  public void onComplete()
  {
    markAsDone();
    _hub.downstreamComplete();
    _subscription = null;
  }

  @Override
  void doCancel()
  {
    if ( null != _subscription )
    {
      _subscription.cancel();
      _subscription = null;
    }
  }

  @Override
  String getQualifiedName()
  {
    return null == _subscription ? "" : _subscription.getQualifiedName();
  }

  @Nullable
  Subscription getSubscription()
  {
    return _subscription;
  }
}
