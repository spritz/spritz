package spritz.examples;

import spritz.ConnectableStream;
import spritz.Stream;

public class Example38
{
  public static void main( String[] args )
  {
    final ConnectableStream<Integer> value$ =
      Stream
        .periodic( 1000 )
        .take( 4 )
        .publishCurrentValue( 23 );

    value$
      .peek( v -> System.out.println( "S1=" + v ) )
      .subscribe( new LoggingSubscriber<>() );

    value$.connect();

    value$
      .peek( v -> System.out.println( "S2=" + v ) )
      .subscribe( new LoggingSubscriber<>() );

    ExampleUtil.run( value$ );
  }
}
