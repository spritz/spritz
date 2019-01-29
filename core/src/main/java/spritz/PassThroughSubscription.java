package spritz;

import javax.annotation.Nonnull;

final class PassThroughSubscription<T>
  extends AbstractOperatorSubscription<T>
{
  PassThroughSubscription( @Nonnull final Subscriber<? super T> subscriber )
  {
    super( subscriber );
  }
}
