package streak.examples;

import streak.Streak;
import streak.schedulers.Schedulers;

public class Example10
{
  public static void main( String[] args )
  {
    Streak
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .switchMap( v -> Streak.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) )
      .afterTerminate( Example10::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( () -> Schedulers.current().shutdown() ).run();
  }
}
