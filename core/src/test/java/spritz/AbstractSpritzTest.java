package spritz;

import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckTestUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import static org.testng.Assert.*;

public abstract class AbstractSpritzTest
{
  @Nonnull
  private final TestLogger _logger = new TestLogger();

  @BeforeMethod
  protected void beforeTest()
  {
    BrainCheckTestUtil.resetConfig( false );
    SpritzTestUtil.resetConfig( false );
    _logger.getEntries().clear();
    SpritzTestUtil.setLogger( _logger );
  }

  @AfterMethod
  protected void afterTest()
  {
    BrainCheckTestUtil.resetConfig( true );
    SpritzTestUtil.resetConfig( true );
  }

  @Nonnull
  protected final TestLogger getTestLogger()
  {
    return _logger;
  }

  @Nonnull
  private SpritzLogger.ProxyLogger getProxyLogger()
  {
    return (SpritzLogger.ProxyLogger) SpritzLogger.getLogger();
  }

  protected final void assertInvariantFailure( @Nonnull final ThrowingRunnable throwingRunnable,
                                               @Nonnull final String message )
  {
    assertEquals( expectThrows( IllegalStateException.class, throwingRunnable ).getMessage(), message );
  }
}
