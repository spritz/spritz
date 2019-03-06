package spritz.examples;

import spritz.Stream;

public class Example34
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream
                       .<Integer>create( c -> {
                         c.next( 1 );
                         c.next( 2 );
                         c.next( 3 );
                         c.error( new Exception( "Bad Stuff" ) );
                       } )
                       .repeat( 3 )
                       .take( 30 ) );
  }
}
