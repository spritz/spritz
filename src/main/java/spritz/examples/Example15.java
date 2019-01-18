package spritz.examples;

import spritz.Spritz;

public class Example15
{
  public static void main( String[] args )
  {
    Spritz.ofNullable( null ).subscribe( new LoggingSubscriber<>() );
    Spritz.ofNullable( 42 ).subscribe( new LoggingSubscriber<>() );
  }
}
