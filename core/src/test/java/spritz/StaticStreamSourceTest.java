package spritz;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class StaticStreamSourceTest
  extends AbstractSpritzTest
{
  @Test
  public void getName_default()
  {
    final Stream<Integer> stream = Stream.of( 1, 2, 3 );
    assertEquals( stream.getName(), "of(1, 2, 3)" );
  }

  @Test
  public void getName_specified()
  {
    final Stream<Integer> stream = Stream.of( "dataIn()", 1, 2, 3 );
    assertEquals( stream.getName(), "dataIn()" );
  }
}
