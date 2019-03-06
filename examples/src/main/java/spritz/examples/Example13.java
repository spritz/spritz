package spritz.examples;

import spritz.Stream;

public class Example13
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.empty().defaultIfEmpty( 23 ) );
  }
}
