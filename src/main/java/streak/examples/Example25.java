package streak.examples;

import streak.Streak;
import streak.schedulers.Schedulers;

public class Example25
{
  public static void main( String[] args )
  {
    Streak
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
    new Thread( () -> Schedulers.current().shutdown() ).run();
  }
}
