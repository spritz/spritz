package spritz.examples;

import spritz.Spritz;

public class Example2
{
  public static void main( String[] args )
  {
    Spritz
      .of( 1, 2, 3, 4 )
      .filter( v -> v > 2 )
      .first()
      .subscribe( new LoggingSubscriber<>() );
  }
}
