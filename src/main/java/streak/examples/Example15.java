package streak.examples;

import streak.Streak;

public class Example15
{
  public static void main( String[] args )
  {
    Streak.context().ofNullable( null ).subscribe( new LoggingSubscriber<>() );
    Streak.context().ofNullable( 42 ).subscribe( new LoggingSubscriber<>() );
  }
}
