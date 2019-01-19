package spritz.examples;

import spritz.Stream;

public class Example5
{
  public static void main( String[] args )
  {
    Stream
      .concat( Stream.of( 1, 2, 3 ), Stream.of( 4, 5, 6 ), Stream.of( 7, 8, 9 ) )
      .skip( 4 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
