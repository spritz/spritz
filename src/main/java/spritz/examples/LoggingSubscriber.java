package spritz.examples;

import javax.annotation.Nonnull;
import spritz.Subscriber;
import spritz.Subscription;

final class LoggingSubscriber<T>
  implements Subscriber<T>
{
  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    System.out.println( "onSubscribe(" + subscription + ")" );
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    System.out.println( "onNext(" + item + ")" );
  }

  @Override
  public void onError( @Nonnull final Throwable throwable )
  {
    System.out.println( "onError(" + throwable + ")" );
  }

  @Override
  public void onComplete()
  {
    System.out.println( "onComplete()" );
  }
}
