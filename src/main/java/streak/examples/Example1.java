package streak.examples;

import streak.Streak;

public class Example1
{
  public static void main( String[] args )
  {
    Streak
      .range( 42, 20 )
      .peek( v -> System.out.println( "Pre Take Peek: " + v ) )
      .take( 5 )
      .peek( v -> System.out.println( "Post Take Peek: " + v ) )
      .onTerminate( () -> System.out.println( "onTerminate()" ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
