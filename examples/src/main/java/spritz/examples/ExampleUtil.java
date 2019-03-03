package spritz.examples;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nonnull;
import spritz.Stream;

public final class ExampleUtil
{
  private ExampleUtil()
  {
  }

  public static <T> void run( @Nonnull final Stream<T> stream )
  {
    final AtomicBoolean complete = new AtomicBoolean();
    stream
      .peekTerminate( () -> complete.set( true ) )
      .subscribe( new LoggingSubscriber<>() );

    while ( !complete.get() )
    {
      try
      {
        Thread.sleep( 10 );
      }
      catch ( final InterruptedException ignored )
      {
      }
    }
  }
}
