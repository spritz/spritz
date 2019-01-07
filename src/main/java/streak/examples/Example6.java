package streak.examples;

import streak.Streak;

public class Example6
{
  public static void main( String[] args )
  {
    Streak
      .periodic( 100 )
      .takeUntil( v -> v > 5 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
