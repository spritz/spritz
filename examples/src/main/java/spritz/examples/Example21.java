package spritz.examples;

import java.util.NoSuchElementException;
import spritz.Stream;

public class Example21
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.empty().errorIfEmpty( NoSuchElementException::new ) );
  }
}
