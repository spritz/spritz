package streak.examples;

import java.util.Arrays;
import streak.Streak;

@SuppressWarnings( "unchecked" )
public class Example9
{
  public static void main( String[] args )
  {
    Streak
      .fromCollection( Arrays.asList( "A", "B", "C", "D", "E" ) )
      .append( Streak.of( "F", "G" ), Streak.of( "H", "I" ) )
      .prepend( Streak.of( "1", "2" ), Streak.of( "3", "4" ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
