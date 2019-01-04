package streak.examples;

import streak.Streak;
import streak.StreakContext;

public class Example10
{
  public static void main( String[] args )
  {
    final StreakContext context = Streak.context();
    context
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .switchMap( v -> context.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
