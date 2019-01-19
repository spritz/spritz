package spritz.examples;

import spritz.Stream;

public class Example16
{
  public static void main( String[] args )
  {
    Stream
      .fromSupplier( () -> "Tick" )
      .take( 12 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
