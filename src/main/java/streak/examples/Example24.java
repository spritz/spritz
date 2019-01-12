package streak.examples;

import streak.Streak;
import streak.schedulers.Schedulers;

public class Example24
{
  public static void main( String[] args )
  {
    Streak
      .periodic( 100 )
      .filter( v -> v < 5 )
      .timeout( 1000 )
      .afterTerminate( Example24::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( () -> Schedulers.current().shutdown() ).run();
  }
}
