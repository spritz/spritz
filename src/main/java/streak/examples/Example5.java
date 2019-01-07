package streak.examples;

import streak.Streak;

public class Example5
{
  public static void main( String[] args )
  {
    Streak
      .concat( Streak.of( 1, 2, 3 ), Streak.of( 4, 5, 6 ), Streak.of( 7, 8, 9 ) )
      .skip( 4 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
