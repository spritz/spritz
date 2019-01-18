package spritz.examples;

import java.util.NoSuchElementException;
import spritz.Spritz;

public class Example21
{
  public static void main( String[] args )
  {
    Spritz
      .empty()
      .errorIfEmpty( NoSuchElementException::new )
      .subscribe( new LoggingSubscriber<>() );
  }
}
