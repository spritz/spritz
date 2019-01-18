package spritz.examples;

import spritz.Spritz;
import spritz.schedulers.Schedulers;

public class Example8
{
  public static void main( String[] args )
  {
    Spritz
      .periodic( 100 )
      .takeUntil( v -> v > 20 )
      .last( 5 )
      .afterTerminate( Example8::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
