package spritz.examples;

import spritz.Stream;

public class Example4
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.of( 1, 1, 1, 1, 1, 2, 2, 2, 1, 3, 3, 4 ).skipRepeats() );
  }
}
