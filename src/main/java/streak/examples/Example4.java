package streak.examples;

import streak.Streak;

public class Example4
{
  public static void main( String[] args )
  {
    Streak
      .context()
      .of( 1, 1, 1, 1, 1, 2, 2, 2, 1, 3, 3, 4 )
      .skipConsecutiveDuplicates()
      .subscribe( new LoggingSubscriber<>() );
  }
}
