package spritz.examples;

import java.io.IOException;
import spritz.Spritz;

public class Example14
{
  public static void main( String[] args )
  {
    Spritz
      .fail( new IOException() )
      .subscribe( new LoggingSubscriber<>() );
  }
}
