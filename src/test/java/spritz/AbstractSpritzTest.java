package spritz;

import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public abstract class AbstractSpritzTest
{
  @BeforeMethod
  protected void beforeTest()
  {
    BrainCheckTestUtil.resetConfig( false );
  }

  @AfterMethod
  protected void afterTest()
  {
    BrainCheckTestUtil.resetConfig( true );
  }

  final void assertInvariantFailure( @Nonnull final ThrowingRunnable throwingRunnable, @Nonnull final String message )
  {
    assertEquals( expectThrows( IllegalStateException.class, throwingRunnable ).getMessage(), message );
  }
}
