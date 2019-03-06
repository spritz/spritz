package spritz.examples;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import spritz.Stream;

public final class ExampleUtil
{
  private ExampleUtil()
  {
  }

  public static <T> void run( @Nonnull final Stream<T> stream )
  {
    run( stream, s -> s.subscribe( new LoggingSubscriber<>() ) );
  }

  public static <T> void run( @Nonnull final Stream<T> stream, @Nonnull final Consumer<Stream<T>> consumer )
  {
    final AtomicBoolean complete = new AtomicBoolean();
    consumer.accept( stream.peekTerminate( () -> complete.set( true ) ) );

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
