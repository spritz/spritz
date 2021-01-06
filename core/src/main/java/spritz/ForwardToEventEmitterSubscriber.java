package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A subscriber that forwards events onto an EventEmitter.
 */
final class ForwardToEventEmitterSubscriber<T>
  extends Subscription
  implements Subscriber<T>
{
  @Nonnull
  private final EventEmitter<T> _emitter;
  @Nullable
  private Subscription _subscription;

  ForwardToEventEmitterSubscriber( @Nonnull final EventEmitter<T> emitter )
  {
    _emitter = Objects.requireNonNull( emitter );
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    _subscription = subscription;
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    _emitter.next( item );
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    markAsDone();
    _emitter.error( error );
    _subscription = null;
  }

  @Override
  public void onComplete()
  {
    markAsDone();
    _emitter.complete();
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
}
