package spritz;

import javax.annotation.Nonnull;

class PassThroughSubscription<T>
  extends AbstractOperatorSubscription<T, T>
{
  PassThroughSubscription( @Nonnull final Subscriber<? super T> subscriber )
  {
    super( subscriber );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onNext( @Nonnull final T item )
  {
    getDownstreamSubscriber().onNext( item );
  }
}
