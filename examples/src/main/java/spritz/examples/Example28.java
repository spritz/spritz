package spritz.examples;

import spritz.Stream;

public class Example28
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .periodic( 50 )
      .filter( v -> !( v > 10 && v < 20 ) )
      .sample( 210 )
      .take( 12 );
    ExampleUtil.run( stream );
  }
}
