package spritz.examples;

import spritz.Spritz;

public class Example3
{
  public static void main( String[] args )
  {
    Spritz
      .range( 42, 20 )
      .dropUntil( v -> v == 55 )
      .map( v -> "*" + v + "*" )
      .forEach( v -> System.out.println( "Bang! " + v ) );
  }
}
