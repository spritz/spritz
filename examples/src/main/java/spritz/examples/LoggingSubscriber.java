package spritz.examples;

import javax.annotation.Nonnull;
import spritz.Scheduler;
import spritz.Subscriber;
import spritz.Subscription;

final class LoggingSubscriber<T>
  implements Subscriber<T>
{
  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    System.out.println( "onSubscribe(" + subscription + ") on " + Scheduler.currentVpu().getName() );
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    System.out.println( "onNext(" + item + ") on " + Scheduler.currentVpu().getName() );
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    System.out.println( "onError(" + error + ") on " + Scheduler.currentVpu().getName() );
  }

  @Override
  public void onComplete()
  {
    System.out.println( "onComplete() on " + Scheduler.currentVpu().getName() );
  }
}
