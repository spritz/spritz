package spritz.examples;

import spritz.Stream;

public class Example16
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.fromSupplier( () -> "Tick" ).take( 12 ) );
  }
}
