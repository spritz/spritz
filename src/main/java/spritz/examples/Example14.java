package spritz.examples;

import java.io.IOException;
import spritz.Stream;

public class Example14
{
  public static void main( String[] args )
  {
    Stream
      .fail( new IOException() )
      .subscribe( new LoggingSubscriber<>() );
  }
}
