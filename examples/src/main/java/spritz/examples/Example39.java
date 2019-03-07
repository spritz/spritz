package spritz.examples;

import spritz.ConnectableStream;
import spritz.Stream;

public class Example39
{
  public static void main( String[] args )
    throws Exception
  {
    final ConnectableStream<Integer> value$ =
      Stream
        .merge( Stream.of( 701, 702, 703, 704, 705 ),
                Stream.periodic( 1000 ).take( 5 ),
                Stream.of( 901, 902, 903, 904, 905 ) )
        .publishReplay( 3, 2000 );

    value$.connect();
    Thread.sleep( 1000 );
    value$.subscribe( new LoggingSubscriber<>("S1") );
    Thread.sleep( 1500 );
    value$.subscribe( new LoggingSubscriber<>("S2") );

    ExampleUtil.run( value$ );
  }
}
