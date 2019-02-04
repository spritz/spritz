package spritz.examples;

import spritz.Spritz;
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
      } );
    source.take( 2 ).afterTerminate( Example29::terminateScheduler ).subscribe( new LoggingSubscriber<>() );
    System.out.println( "Second Materialization of Source" );
    source.afterTerminate( Example29::terminateScheduler ).subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Spritz::shutdown ).run();
  }
}
