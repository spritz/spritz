package streak.examples;

import java.io.IOException;
import streak.Streak;

public class Example14
{
  public static void main( String[] args )
  {
    Streak
      .fail( new IOException() )
      .subscribe( new LoggingSubscriber<>() );
  }
}
