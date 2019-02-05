package spritz.examples;

import spritz.Stream;

public class Example27
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .periodic( 100 )
      .peek( v -> System.out.println( "Ping @ " + v ) )
      .throttle( 150 )
      .peek( v -> System.out.println( "Ding @ " + v ) )
      .take( 5 );
    ExampleUtil.run( stream );
  }
}
