package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A subscriber that forwards events onto a subject.
 */
final class ForwardToSubjectSubscriber<T>
  implements Subscriber<T>
{
  @Nonnull
  private final Subject<T> _subject;
  @Nullable
  private Subscription _subscription;

  ForwardToSubjectSubscriber( @Nonnull final Subject<T> subject )
  {
    _subject = Objects.requireNonNull( subject );
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    _subscription = subscription;
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    _subject.doNext( item );
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    _subject.doError( error );
    _subscription = null;
  }

  @Override
  public void onComplete()
  {
    _subject.doComplete();
    _subscription = null;
  }

  void cancel()
  {
    if ( null != _subscription )
    {
      _subscription.cancel();
      _subscription = null;
    }
  }
}
