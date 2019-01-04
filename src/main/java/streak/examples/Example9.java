package streak.examples;

import java.util.Arrays;
import streak.Streak;
import streak.StreakContext;

@SuppressWarnings( "unchecked" )
public class Example9
{
  public static void main( String[] args )
  {
    final StreakContext context = Streak.context();
    context
      .fromCollection( Arrays.asList( "A", "B", "C", "D", "E" ) )
      .append( context.of( "F", "G" ), context.of( "H", "I" ) )
      .prepend( context.of( "1", "2" ), context.of( "3", "4" ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
