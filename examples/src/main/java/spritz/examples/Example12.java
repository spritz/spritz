package spritz.examples;

import spritz.Stream;

public class Example12
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream
                       .of( 1, 2, 3, 4, 5, 6, 7, 8, 9 )
                       .scan( ( e, sum ) -> e + sum, 0 ) );
  }
}
