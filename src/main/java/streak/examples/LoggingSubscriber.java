package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;

final class LoggingSubscriber<T>
  implements Flow.Subscriber<T>
{
  @Override
  public void onSubscribe( @Nonnull final Flow.Subscription subscription )
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
    throwable.printStackTrace();
  }

  @Override
  public void onComplete()
  {
    System.out.println( "onComplete()" );
  }
}
