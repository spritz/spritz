package spritz.examples;

import spritz.Spritz;
import spritz.schedulers.Schedulers;

public class Example27
{
  public static void main( String[] args )
  {
    Spritz
      .periodic( 100 )
      .peek( v -> System.out.println( "Ping @ " + v ) )
      .throttle( 150 )
      .peek( v -> System.out.println( "Ding @ " + v ) )
      .take( 5 )
      .afterTerminate( Example27::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
