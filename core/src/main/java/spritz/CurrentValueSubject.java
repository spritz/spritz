package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A CurrentValueSubject will emit the current value to downstream {@link Subscriber}s when they subscribe and then
 * any items that are emitted subsequent to the time of the subscription. The current value is either the
 * last value emitted or was an initial value specified when creating the subject.
 */
final class CurrentValueSubject<T>
  extends Subject<T>
{
  @Nonnull
  private T _currentValue;

  CurrentValueSubject( @Nullable final String name, @Nonnull final T initialValue )
  {
    super( Spritz.areNamesEnabled() ?
           generateName( name, "currentValueSubject", String.valueOf( initialValue ) ) :
           null );
    _currentValue = Objects.requireNonNull( initialValue );
  }

  @Override
  void completeSubscribe( @Nonnull final DownstreamSubscription subscription )
  {
    if ( isNotDone() && subscription.isNotDone() )
    {
      subscription.getSubscriber().onItem( _currentValue );
    }
  }

  @Override
  void downstreamNext( @Nonnull final T item )
  {
    super.downstreamNext( item );
    _currentValue = item;
  }
}
