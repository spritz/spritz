package spritz.examples;

import spritz.Stream;

public class Example29
{
  public static void main( String[] args )
  {
    // FN-2187
    final Stream<Object> source = Stream
      .create( s -> {
        s.next( 'F' );
        s.next( 'N' );
        s.next( '2' );
        if ( !s.isCancelled() )
        {
          s.next( '1' );
          s.next( '8' );
          s.next( '7' );
        }
        s.complete();
      } )
      .peekSubscribe( s -> System.out.println( "peekSubscribe => " + s ) )
      .peek( s -> System.out.println( "peek => " + s ) )
      .peekError( s -> System.out.println( "peekError => " + s ) )
      .peekComplete( () -> System.out.println( "peekComplete " ) );
    ExampleUtil.run( source.take( 2 ) );
    System.out.println( "Second Materialization of Source" );
    ExampleUtil.run( source );
  }
}
