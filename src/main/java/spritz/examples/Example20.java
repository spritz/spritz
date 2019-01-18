package spritz.examples;

import spritz.Spritz;

public class Example20
{
  public static void main( String[] args )
  {
    Spritz
      .of( 1.3, 2, 3.0, 4 )
      .ofType( Integer.class )
      .subscribe( new LoggingSubscriber<>() );
  }
}
