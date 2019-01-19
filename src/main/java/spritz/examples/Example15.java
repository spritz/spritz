package spritz.examples;

import spritz.Stream;

public class Example15
{
  public static void main( String[] args )
  {
    Stream.ofNullable( null ).subscribe( new LoggingSubscriber<>() );
    Stream.ofNullable( 42 ).subscribe( new LoggingSubscriber<>() );
  }
}
