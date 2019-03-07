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
    super( name );
    _currentValue = Objects.requireNonNull( initialValue );
  }

  @Override
  void completeSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    super.completeSubscribe( subscriber );
    if ( !isDone() && isSubscriber( subscriber ) )
    {
      subscriber.onNext( _currentValue );
    }
  }

  @Override
  void doNext( @Nonnull final T item )
  {
    super.doNext( item );
    _currentValue = item;
  }
}
