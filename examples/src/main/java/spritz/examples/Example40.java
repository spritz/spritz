package spritz.examples;

import spritz.Stream;

public class Example40
{
  public static void main( String[] args )
    throws Exception
  {
    final Stream<Integer> value$ =
      Stream
        .periodic( 1000 )
        .take( 4 )
        .publishReplay().refCount();

    value$.subscribe( new LoggingSubscriber<>( "S1" ) );

    Thread.sleep( 2500 );

    value$.subscribe( new LoggingSubscriber<>( "S2" ) );

    ExampleUtil.run( value$ );
  }
}
