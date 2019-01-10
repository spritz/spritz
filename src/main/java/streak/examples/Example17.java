package streak.examples;

import streak.Streak;
import streak.schedulers.Schedulers;

public class Example17
{
  public static void main( String[] args )
  {
    Streak
      .generate( () -> "Tick", 200 )
      .take( 12 )
      .afterTerminate( Example17::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( () -> Schedulers.current().shutdown() ).run();
  }
}
