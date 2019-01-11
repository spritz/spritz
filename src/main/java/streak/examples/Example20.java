package streak.examples;

import streak.Streak;

public class Example20
{
  public static void main( String[] args )
  {
    Streak
      .of( 1.3, 2, 3.0, 4 )
      .ofType( Integer.class )
      .subscribe( new LoggingSubscriber<>() );
  }
}
