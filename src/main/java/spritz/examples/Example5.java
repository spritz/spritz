package spritz.examples;

import spritz.Spritz;

public class Example5
{
  public static void main( String[] args )
  {
    Spritz
      .concat( Spritz.of( 1, 2, 3 ), Spritz.of( 4, 5, 6 ), Spritz.of( 7, 8, 9 ) )
      .skip( 4 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
