package spritz;

import java.io.File;
import javax.annotation.Nonnull;
import org.realityforge.braincheck.BrainCheckTestUtil;
import org.realityforge.braincheck.GuardMessageCollector;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import static org.testng.Assert.*;

public abstract class AbstractTest
{
  private static final GuardMessageCollector c_messages = createCollector();
  @Nonnull
  private final TestLogger _logger = new TestLogger();

  @BeforeSuite
  protected void beforeSuite()
  {
    c_messages.onTestSuiteStart();
  }

  @AfterSuite
  protected void afterSuite()
  {
    c_messages.onTestSuiteComplete();
  }

  @BeforeMethod
  protected void beforeTest()
  {
    BrainCheckTestUtil.resetConfig( false );
    SpritzTestUtil.resetConfig( false );
    _logger.getEntries().clear();
    SpritzTestUtil.setLogger( _logger );
    c_messages.onTestStart();
  }

  @AfterMethod
  protected void afterTest()
  {
    c_messages.onTestComplete();
    BrainCheckTestUtil.resetConfig( true );
    SpritzTestUtil.resetConfig( true );
  }

  @Nonnull
  protected final TestLogger getTestLogger()
  {
    return _logger;
  }

  protected final void assertInvariantFailure( @Nonnull final ThrowingRunnable throwingRunnable,
                                               @Nonnull final String message )
  {
    assertEquals( expectThrows( IllegalStateException.class, throwingRunnable ).getMessage(), message );
  }

  @Nonnull
  private static GuardMessageCollector createCollector()
  {
    final boolean saveIfChanged = "true".equals( System.getProperty( "spritz.output_fixture_data", "false" ) );
    final String fixtureDir = System.getProperty( "spritz.diagnostic_messages_file" );
    assertNotNull( fixtureDir,
                   "Expected System.getProperty( \"spritz.diagnostic_messages_file\" ) to return location of diagnostic messages file" );
    return new GuardMessageCollector( "spritz", new File( fixtureDir ), saveIfChanged );
  }
}
