package streak.examples;

import streak.Streak;

public class Example22
{
  public static void main( String[] args )
  {
    Streak.empty().firstOrError().subscribe( new LoggingSubscriber<>() );
    Streak.of( 1 ).firstOrError().subscribe( new LoggingSubscriber<>() );
    Streak.empty().firstOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
    Streak.of( 1 ).firstOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
  }
}
