package spritz.examples;

import spritz.Stream;

public class Example15
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.ofNullable( null ) );
    ExampleUtil.run( Stream.ofNullable( 42 ) );
  }
}
