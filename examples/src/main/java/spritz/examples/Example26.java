package spritz.examples;

import spritz.Stream;

public class Example26
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .periodic( 100 )
      .filter( v -> v < 5 )
      .peek( v -> System.out.println( "Ping @ " + v ) )
      .debounce( v -> v * 50 )
      .peek( v -> System.out.println( "Ding @ " + v ) )
      .take( 3 );
    ExampleUtil.run( stream );
  }
}
