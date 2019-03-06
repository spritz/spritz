package spritz.examples;

import spritz.Stream;

public class Example32
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream
                       .<Integer>create( c -> c.error( new Exception( "Bad Stuff" ) ) )
                       .rescue( error -> Stream.of( 1, 2, 3 ) ) );
  }
}
