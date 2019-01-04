package streak.examples;

import streak.Streak;

public class Example2
{
  public static void main( String[] args )
  {
    Streak
      .context()
      .of( 1, 2, 3, 4 )
      .filter( v -> v > 2 )
      .first()
      .subscribe( new LoggingSubscriber<>() );
  }
}
