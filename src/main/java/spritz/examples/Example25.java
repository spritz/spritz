package spritz.examples;

import spritz.Spritz;
import spritz.schedulers.Schedulers;

public class Example25
{
  public static void main( String[] args )
  {
    Spritz
      .periodic( 100 )
      .filter( v -> v < 5 )
      .peek( v -> System.out.println( "Ping @ " + v ) )
      .debounce( 1000 )
      .peek( v -> System.out.println( "Ding @ " + v ) )
      .afterTerminate( Example25::terminateScheduler )
      .first()
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
