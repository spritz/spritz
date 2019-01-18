package spritz.examples;

import spritz.Spritz;

public class Example22
{
  public static void main( String[] args )
  {
    Spritz.empty().firstOrError().subscribe( new LoggingSubscriber<>() );
    Spritz.of( 1 ).firstOrError().subscribe( new LoggingSubscriber<>() );
    Spritz.empty().firstOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
    Spritz.of( 1 ).firstOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
  }
}
