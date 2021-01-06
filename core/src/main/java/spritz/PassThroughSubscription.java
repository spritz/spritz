package spritz;

import javax.annotation.Nonnull;

class PassThroughSubscription<T, StreamT extends Stream<T>>
  extends AbstractOperatorSubscription<T, T, StreamT>
{
  PassThroughSubscription( @Nonnull final StreamT stream, @Nonnull final Subscriber<? super T> subscriber )
  {
    super( stream, subscriber );
  }

  @Override
  public void onItem( @Nonnull final T item )
  {
    getSubscriber().onItem( item );
  }
}
