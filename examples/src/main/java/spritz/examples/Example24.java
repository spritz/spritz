package spritz.examples;

import spritz.Stream;

public class Example24
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.periodic( 100 ).filter( v -> v < 5 ).timeout( 1000 ) );
  }
}
