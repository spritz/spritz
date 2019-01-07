package streak.examples;

import streak.Streak;

public class Example11
{
  public static void main( String[] args )
  {
    Streak
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .exhaustMap( v -> Streak.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
