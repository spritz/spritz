package spritz.examples;

import spritz.Spritz;

public class Example13
{
  public static void main( String[] args )
  {
    Spritz
      .empty()
      .defaultIfEmpty( 23 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
