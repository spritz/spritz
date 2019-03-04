package spritz.examples;

import spritz.Stream;

public class Example36
{
  public static void main( String[] args )
  {
    final Stream<Integer> ticker$ = Stream
      .periodic( 1000 )
      .takeUntil( v11 -> v11 > 10 ).peek( v12 -> System.out.println( "Tick " + v12 ) )
      .publish();

    ticker$.filter( v4 -> v4 % 2 == 0 )
      .peek( v5 -> System.out.println( "T1=" + v5 ) )
      .subscribe( new LoggingSubscriber<>() );
    ticker$.filter( v2 -> v2 % 3 == 0 )
      .peek( v3 -> System.out.println( "T2=" + v3 ) )
      .subscribe( new LoggingSubscriber<>() );
    ticker$.filter( v -> v % 4 == 0 )
      .peek( v1 -> System.out.println( "T3=" + v1 ) )
      .subscribe( new LoggingSubscriber<>() );

    ExampleUtil.run( ticker$ );
  }
}
