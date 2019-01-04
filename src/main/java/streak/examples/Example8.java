package streak.examples;

import streak.Streak;

public class Example8
{
  public static void main( String[] args )
  {
    Streak
      .context()
      .periodic( 100 )
      .takeUntil( v -> v > 20 )
      .last( 5 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
