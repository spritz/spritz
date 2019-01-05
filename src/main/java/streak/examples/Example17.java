package streak.examples;

import streak.Streak;

public class Example17
{
  public static void main( String[] args )
  {
    Streak.context()
      .generate( () -> "Tick", 200 )
      .take( 12 )
      .subscribe( new LoggingSubscriber<>() );
  }
}
