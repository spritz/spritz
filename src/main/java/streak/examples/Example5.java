package streak.examples;

import streak.Streak;
import streak.StreakContext;

public class Example5
{
  @SuppressWarnings( "unchecked" )
  public static void main( String[] args )
  {
    final StreakContext context = Streak.context();
    context
      .concat( context.of( 1, 2, 3 ), context.of( 4, 5, 6 ), context.of( 7, 8, 9 ) )
      .skip( 4 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
