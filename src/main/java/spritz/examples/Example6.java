package spritz.examples;

import spritz.Stream;
import spritz.schedulers.Schedulers;

public class Example6
{
  public static void main( String[] args )
  {
    Stream
      .periodic( 100 )
      .takeUntil( v -> v > 5 )
      .afterTerminate( Example6::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
