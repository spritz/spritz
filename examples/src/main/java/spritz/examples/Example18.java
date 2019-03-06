package spritz.examples;

import java.util.concurrent.atomic.AtomicInteger;
import spritz.Stream;

public class Example18
{
  public static void main( String[] args )
  {
    final AtomicInteger counter = new AtomicInteger();
    ExampleUtil.run( Stream.generate( counter::incrementAndGet, 50 ).sample( 210 ).take( 12 ) );
  }
}
