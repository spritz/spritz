package spritz.examples;

import spritz.Stream;

public class Example23
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.empty().lastOrError() );
    ExampleUtil.run( Stream.of( 1, 2, 3 ).lastOrError() );
    ExampleUtil.run( Stream.empty().lastOrDefault( 22 ) );
    ExampleUtil.run( Stream.of( 1, 2, 3 ).lastOrDefault( 22 ) );
  }
}
