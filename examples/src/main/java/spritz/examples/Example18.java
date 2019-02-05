package spritz.examples;

import java.util.concurrent.atomic.AtomicInteger;
import spritz.Stream;

public class Example18
{
  public static void main( String[] args )
  {
    final AtomicInteger counter = new AtomicInteger();
    final Stream<Integer> stream = Stream
      .generate( counter::incrementAndGet, 50 )
      .sample( 210 )
      .take( 12 );
    ExampleUtil.run( stream );
  }
}
