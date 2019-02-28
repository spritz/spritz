package spritz.examples;

import spritz.Stream;

public class Example36
{
  public static void main( String[] args )
  {
    final Stream<Integer> ticker$ = Stream
      .periodic( 1000 )
      .takeUntil( v1 -> v1 > 10 )
      .tap( v1 -> System.out.println( "Tick " + v1 ) )
      .publish();

    ticker$.filter( v -> v % 2 == 0 ).tap( v -> System.out.println( "T1=" + v ) ).subscribe( new LoggingSubscriber<>() );
    ticker$.filter( v -> v % 3 == 0 ).tap( v -> System.out.println( "T2=" + v ) ).subscribe( new LoggingSubscriber<>() );
    ticker$.filter( v -> v % 4 == 0 ).tap( v -> System.out.println( "T3=" + v ) ).subscribe( new LoggingSubscriber<>() );

    ExampleUtil.run( ticker$ );
  }
}
