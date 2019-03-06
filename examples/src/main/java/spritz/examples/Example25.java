package spritz.examples;

import spritz.Stream;

public class Example25
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream
                       .periodic( 100 )
                       .filter( v -> v < 5 )
                       .peek( v -> System.out.println( "Ping @ " + v ) )
                       .debounce( 1000 )
                       .peek( v -> System.out.println( "Ding @ " + v ) )
                       .first() );
  }
}
