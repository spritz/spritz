package streak.examples;

import java.util.NoSuchElementException;
import streak.Streak;

public class Example21
{
  public static void main( String[] args )
  {
    Streak
      .empty()
      .errorIfEmpty( NoSuchElementException::new )
      .subscribe( new LoggingSubscriber<>() );
  }
}
