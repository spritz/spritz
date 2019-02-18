package spritz;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class SpritzTestUtilTest
  extends AbstractSpritzTest
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
  public void purgeTasksWhenRunawayDetected()
  {
    SpritzTestUtil.noPurgeTasksWhenRunawayDetected();
    assertFalse( Spritz.purgeTasksWhenRunawayDetected() );
    SpritzTestUtil.purgeTasksWhenRunawayDetected();
    assertTrue( Spritz.purgeTasksWhenRunawayDetected() );
  }

  @Test
  public void shouldValidateSubscriptions()
  {
    SpritzTestUtil.noValidateSubscriptions();
    assertFalse( Spritz.shouldValidateSubscriptions() );
    SpritzTestUtil.validateSubscriptions();
    assertTrue( Spritz.shouldValidateSubscriptions() );
  }

  @Test
  public void areUncaughtErrorHandlersEnabled()
  {
    SpritzTestUtil.disableUncaughtErrorHandlers();
    assertFalse( Spritz.areUncaughtErrorHandlersEnabled() );
    SpritzTestUtil.enableUncaughtErrorHandlers();
    assertTrue( Spritz.areUncaughtErrorHandlersEnabled() );
  }
}
