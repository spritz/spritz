package spritz.examples;

import java.util.Arrays;
import spritz.Stream;

public class Example9
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream
                       .fromCollection( Arrays.asList( "A", "B", "C", "D", "E" ) )
                       .append( Stream.of( "F", "G" ), Stream.of( "H", "I" ) )
                       .prepend( Stream.of( "1", "2" ), Stream.of( "3", "4" ) ) );
  }
}
