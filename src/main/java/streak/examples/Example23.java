package streak.examples;

import streak.Streak;

public class Example23
{
  public static void main( String[] args )
  {
    Streak.empty().lastOrError().subscribe( new LoggingSubscriber<>() );
    Streak.of( 1, 2, 3 ).lastOrError().subscribe( new LoggingSubscriber<>() );
    Streak.empty().lastOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
    Streak.of( 1, 2, 3 ).lastOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
  }
}
