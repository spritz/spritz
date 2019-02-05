package spritz.examples;

import spritz.Stream;

public class Example11
{
  public static void main( String[] args )
  {
    final Stream<String> stream = Stream
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .exhaustMap( v -> Stream.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) );
    ExampleUtil.run( stream );
  }
}
