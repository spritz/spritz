package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A PublishSubject emits to downstream {@link Subscriber}s only those items that are emitted subsequent
 * to the time of the subscription.
 */
final class PublishSubject<T>
  extends Subject<T>
{
  PublishSubject( @Nullable final String name )
  {
    super( name );
  }

  void completeSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( () -> removeSubscriber( subscriber ) );
    if ( isComplete() )
    {
      subscriber.onComplete();
    }
    else if ( null != getError() )
    {
      subscriber.onError( getError() );
    }
    else
    {
      addSubscriber( subscriber );
    }
  }
}
