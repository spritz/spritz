package spritz.examples;

import spritz.Stream;

public class Example10
{
  public static void main( String[] args )
  {
    final Stream<String> stream = Stream
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .switchMap( v -> Stream.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) );
    ExampleUtil.run( stream );
  }
}
