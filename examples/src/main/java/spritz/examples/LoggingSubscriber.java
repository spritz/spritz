package spritz.examples;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.Scheduler;
import spritz.Subscriber;
import spritz.Subscription;

final class LoggingSubscriber<T>
  implements Subscriber<T>
{
  @Nullable
  private final String _prefix;

  LoggingSubscriber()
  {
    this( null );
  }

  LoggingSubscriber( @Nullable final String prefix )
  {
    _prefix = prefix;
  }

  @Override
  public void onSubscribe( @Nonnull final Subscription subscription )
  {
    System.out.println( prefix() + "onSubscribe(" + subscription + ")" + suffix() );
  }

  @Override
  public void onNext( @Nonnull final T item )
  {
    System.out.println( prefix() + "onNext(" + item + ")" + suffix() );
  }

  @Override
  public void onError( @Nonnull final Throwable error )
  {
    System.out.println( prefix() + "onError(" + error + ")" + suffix() );
  }

  @Override
  public void onComplete()
  {
    System.out.println( prefix() + "onComplete()" + suffix() );
  }

  @Nonnull
  private String prefix()
  {
    return null == _prefix ? "" : _prefix + ":";
  }

  @Nonnull
  private String suffix()
  {
    return " on " + Scheduler.currentVpu().getName();
  }

}
