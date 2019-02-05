package spritz.examples;

import spritz.Stream;

public class Example17
{
  public static void main( String[] args )
  {
    final Stream<String> stream = Stream
      .generate( () -> "Tick", 200 )
      .take( 12 );
    ExampleUtil.run( stream );
  }
}
