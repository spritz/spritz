package spritz.test;

import org.testng.annotations.Test;
import spritz.AbstractTest;
import spritz.Spritz;
import spritz.SpritzTestUtil;
import static org.testng.Assert.*;

public class SpritzTestUtilTest
  extends AbstractTest
{
  @Test
  public void areNamesEnabled()
  {
    SpritzTestUtil.disableNames();
    assertFalse( Spritz.areNamesEnabled() );
    SpritzTestUtil.enableNames();
    assertTrue( Spritz.areNamesEnabled() );
  }

  @Test
  public void shouldValidateSubscriptions()
  {
    SpritzTestUtil.noValidateSubscriptions();
    assertFalse( Spritz.shouldValidateSubscriptions() );
    SpritzTestUtil.validateSubscriptions();
    assertTrue( Spritz.shouldValidateSubscriptions() );
  }
}
