package spritz.examples;

import spritz.Spritz;

public class Example4
{
  public static void main( String[] args )
  {
    Spritz
      .of( 1, 1, 1, 1, 1, 2, 2, 2, 1, 3, 3, 4 )
      .skipConsecutiveDuplicates()
      .subscribe( new LoggingSubscriber<>() );
  }
}
