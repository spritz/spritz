package streak.examples;

import arez.Disposable;
import streak.Streak;
import streak.schedulers.Schedulers;

public class Example8
{
  public static void main( String[] args )
  {
    Streak
      .periodic( 100 )
      .takeUntil( v -> v > 20 )
      .last( 5 )
      .afterTerminate( Example8::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( () -> Disposable.dispose( Schedulers.current() ) ).run();
  }
}
