package spritz.examples;

import spritz.Stream;

public class Example17
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.generate( () -> "Tick", 200 ).take( 12 ) );
  }
}
