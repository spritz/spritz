package spritz.examples;

import spritz.ConnectableStream;
import spritz.Stream;

public class Example36
{
  public static void main( String[] args )
  {
    final ConnectableStream<Integer> ticker$ =
      Stream
        .periodic( 1000 )
        .takeUntil( v -> v > 10 )
        .peek( v -> System.out.println( "Tick " + v ) )
        .publish();

    ticker$
      .filter( v -> v % 2 == 0 )
      .peek( v -> System.out.println( "T1=" + v ) )
      .subscribe( new LoggingSubscriber<>() );
    ticker$
      .filter( v2 -> v2 % 3 == 0 )
      .peek( v3 -> System.out.println( "T2=" + v3 ) )
      .subscribe( new LoggingSubscriber<>() );
    ticker$
      .filter( v -> v % 4 == 0 )
      .peek( v1 -> System.out.println( "T3=" + v1 ) )
      .subscribe( new LoggingSubscriber<>() );

    ticker$.connect();
    ExampleUtil.run( ticker$ );
  }
}
