package streak.examples;

import streak.Streak;

public class Example13
{
  public static void main( String[] args )
  {
    Streak
      .empty()
      .defaultIfEmpty( 23 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
