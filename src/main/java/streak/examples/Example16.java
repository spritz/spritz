package streak.examples;

import streak.Streak;

public class Example16
{
  public static void main( String[] args )
  {
    Streak.context()
      .generate( () -> "Tick" )
      .take( 12 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
