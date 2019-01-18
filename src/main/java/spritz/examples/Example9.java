package spritz.examples;

import java.util.Arrays;
import spritz.Spritz;

public class Example9
{
  public static void main( String[] args )
  {
    Spritz
      .fromCollection( Arrays.asList( "A", "B", "C", "D", "E" ) )
      .append( Spritz.of( "F", "G" ), Spritz.of( "H", "I" ) )
      .prepend( Spritz.of( "1", "2" ), Spritz.of( "3", "4" ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
