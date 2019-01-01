package streak.examples;

import streak.Streak;

public class Example3
{
  public static void main( String[] args )
  {
    Streak
      .context()
      .range( 42, 20 )
      .dropUntil( v -> v == 55 )
      .map( v -> "*" + v + "*" )
      .forEach( v -> System.out.println( "Bang! " + v ) );
  }
}
