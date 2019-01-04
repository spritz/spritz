package streak.examples;

import streak.Streak;
import streak.StreakContext;

public class Example7
{
  @SuppressWarnings( "unchecked" )
  public static void main( String[] args )
  {
    final StreakContext context = Streak.context();
    context
      .merge( context.periodic( 100 ).takeUntil( v -> v > 5 ).map( v -> "A" + v ),
              context.periodic( 50 ).takeUntil( v -> v > 30 ).map( v -> "B" + v ),
              context.periodic( 1000 ).takeUntil( v -> v > 3 ).map( v -> "C" + v ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
