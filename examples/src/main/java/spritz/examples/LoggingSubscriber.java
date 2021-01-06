package spritz.examples;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.Subscriber;
import spritz.Subscription;
import zemeckis.VirtualProcessorUnit;
import zemeckis.Zemeckis;

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
  public void onItem( @Nonnull final T item )
  {
    System.out.println( prefix() + "onItem(" + item + ")" + suffix() );
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
    final VirtualProcessorUnit currentVpu = Zemeckis.currentVpu();
    return null == currentVpu ? "" : " on " + currentVpu.getName();
  }
}
