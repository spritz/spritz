package spritz.test;

import org.testng.annotations.Test;
import spritz.AbstractSpritzTest;
import spritz.Spritz;
import spritz.SpritzTestUtil;
import static org.testng.Assert.*;

public class SpritzTestUtilTest
  extends AbstractSpritzTest
{
  @Test
  public void purgeTasksWhenRunawayDetected()
  {
    SpritzTestUtil.noPurgeTasksWhenRunawayDetected();
    assertFalse( Spritz.purgeTasksWhenRunawayDetected() );
    SpritzTestUtil.purgeTasksWhenRunawayDetected();
    assertTrue( Spritz.purgeTasksWhenRunawayDetected() );
  }

  @Test
  public void validateSubscriptions()
  {
    SpritzTestUtil.noValidateSubscriptions();
    assertFalse( Spritz.shouldValidateSubscriptions() );
    SpritzTestUtil.validateSubscriptions();
    assertTrue( Spritz.shouldValidateSubscriptions() );
  }
}
