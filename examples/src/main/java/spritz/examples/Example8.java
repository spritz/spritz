package spritz.examples;

import spritz.Stream;

public class Example8
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.periodic( 100 ).takeUntil( v -> v > 20 ).last( 5 ) );
  }
}
