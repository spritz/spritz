package spritz.examples;

import spritz.Spritz;
import spritz.Stream;

public class Example24
{
  public static void main( String[] args )
  {
    Stream
      .periodic( 100 )
      .filter( v -> v < 5 )
      .timeout( 1000 )
      .afterTerminate( Example24::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Spritz::shutdown ).run();
  }
}
