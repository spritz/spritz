package spritz.examples;

import spritz.Spritz;

public class Example23
{
  public static void main( String[] args )
  {
    Spritz.empty().lastOrError().subscribe( new LoggingSubscriber<>() );
    Spritz.of( 1, 2, 3 ).lastOrError().subscribe( new LoggingSubscriber<>() );
    Spritz.empty().lastOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
    Spritz.of( 1, 2, 3 ).lastOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
  }
}
