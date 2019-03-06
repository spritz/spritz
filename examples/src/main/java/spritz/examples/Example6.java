package spritz.examples;

import spritz.Stream;

public class Example6
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.periodic( 100 ).takeUntil( v -> v > 5 ) );
  }
}
