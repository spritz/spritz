package spritz.examples;

import spritz.Spritz;
import spritz.schedulers.Schedulers;

public class Example26
{
  public static void main( String[] args )
  {
    Spritz
      .periodic( 100 )
      .filter( v -> v < 5 )
      .peek( v -> System.out.println( "Ping @ " + v ) )
      .debounce( v -> v * 50 )
      .peek( v -> System.out.println( "Ding @ " + v ) )
      .take( 3 )
      .afterTerminate( Example26::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
