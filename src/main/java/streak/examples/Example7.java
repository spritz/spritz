package streak.examples;

import streak.Streak;
import streak.schedulers.Schedulers;

public class Example7
{
  public static void main( String[] args )
  {
    Streak
      .merge( Streak.periodic( 100 ).takeUntil( v -> v > 5 ).map( v -> "A" + v ),
              Streak.periodic( 50 ).takeUntil( v -> v > 30 ).map( v -> "B" + v ),
              Streak.periodic( 1000 ).takeUntil( v -> v > 3 ).map( v -> "C" + v ) )
      .afterTerminate( Example7::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
