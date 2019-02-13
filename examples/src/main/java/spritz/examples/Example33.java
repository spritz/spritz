package spritz.examples;

import spritz.Stream;

public class Example33
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .<Integer>create( c -> {
        c.next( 1 );
        c.next( 2 );
        c.next( 3 );
        c.error( new Exception( "Bad Stuff" ) );
      } )
      .repeat()
      .take( 10 );
    ExampleUtil.run( stream );
  }
}
