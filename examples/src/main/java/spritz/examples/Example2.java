package spritz.examples;

import spritz.Stream;

public class Example2
{
  public static void main( String[] args )
  {
    Stream
      .of( 1, 2, 3, 4 )
      .filter( v -> v > 2 )
      .first()
      .subscribe( new LoggingSubscriber<>() );
  }
}
