package spritz.examples;

import java.util.concurrent.atomic.AtomicInteger;
import spritz.Spritz;
import spritz.Stream;

public class Example18
{
  public static void main( String[] args )
  {
    final AtomicInteger counter = new AtomicInteger();
    Stream
      .generate( counter::incrementAndGet, 50 )
      .sample( 210 )
      .take( 12 )
      .afterTerminate( Example18::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Spritz::shutdown ).run();
  }
}
