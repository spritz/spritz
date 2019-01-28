package spritz.examples;

import spritz.Stream;
import spritz.schedulers.Schedulers;

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
    new Thread( Schedulers::shutdown ).run();
  }
}