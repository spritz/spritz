package spritz.examples;

import spritz.Stream;

public class Example22
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.empty().firstOrError() );
    ExampleUtil.run( Stream.of( 1 ).firstOrError() );
    ExampleUtil.run( Stream.empty().firstOrDefault( 22 ) );
    ExampleUtil.run( Stream.of( 1 ).firstOrDefault( 22 ) );
  }
}
