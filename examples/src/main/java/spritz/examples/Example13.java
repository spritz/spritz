package spritz.examples;

import spritz.Stream;

public class Example13
{
  public static void main( String[] args )
  {
    Stream
      .empty()
      .defaultIfEmpty( 23 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
