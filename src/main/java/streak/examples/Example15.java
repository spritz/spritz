package streak.examples;

import streak.Streak;

public class Example15
{
  public static void main( String[] args )
  {
    Streak.ofNullable( null ).subscribe( new LoggingSubscriber<>() );
    Streak.ofNullable( 42 ).subscribe( new LoggingSubscriber<>() );
  }
}
