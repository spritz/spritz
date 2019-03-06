package spritz.examples;

import spritz.Stream;

public class Example20
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.of( 1.3, 2, 3.0, 4 ).ofType( Integer.class ) );
  }
}
