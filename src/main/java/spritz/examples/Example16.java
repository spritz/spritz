package spritz.examples;

import spritz.Spritz;

public class Example16
{
  public static void main( String[] args )
  {
    Spritz
      .fromSupplier( () -> "Tick" )
      .take( 12 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
