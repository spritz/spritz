package spritz.test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.Test;
import spritz.AbstractTest;
import spritz.Spritz;
import spritz.SpritzTestUtil;
import spritz.Stream;
import spritz.TestLogger;
import spritz.UncaughtErrorHandler;
import static org.testng.Assert.*;

public class UncaughtErrorHandlerTest
  extends AbstractTest
{
  @Test
  public void basicOperation()
  {
    final Throwable throwable = new IllegalStateException();

    final AtomicInteger callCount = new AtomicInteger();

    final UncaughtErrorHandler handler = ( throwableArg ) -> {
      callCount.incrementAndGet();
      assertEquals( throwableArg, throwable );
    };
    Spritz.addUncaughtErrorHandler( handler );

    Spritz.reportUncaughtError( throwable );

    assertEquals( callCount.get(), 1 );

    Spritz.reportUncaughtError( throwable );

    assertEquals( callCount.get(), 2 );

    Spritz.removeUncaughtErrorHandler( handler );

    Spritz.reportUncaughtError( throwable );

    // Not called again
    assertEquals( callCount.get(), 2 );
  }

  @Test
  public void addUncaughtErrorHandler_alreadyExists()
  {
    final UncaughtErrorHandler handler = e -> {
    };
    Spritz.addUncaughtErrorHandler( handler );

    assertInvariantFailure( () -> Spritz.addUncaughtErrorHandler( handler ),
                            "Spritz-0096: Attempting to add handler " + handler + " that is already in " +
                            "the list of error handlers." );
  }

  @Test
  public void removeUncaughtErrorHandler_noExists()
  {
    final UncaughtErrorHandler handler = e -> {
    };

    assertInvariantFailure( () -> Spritz.removeUncaughtErrorHandler( handler ),
                            "Spritz-0097: Attempting to remove handler " + handler + " that is not in " +
                            "the list of error handlers." );
  }

  @Test
  public void multipleHandlers()
  {
    final Throwable throwable = new IllegalStateException();

    final AtomicInteger callCount1 = new AtomicInteger();
    final AtomicInteger callCount2 = new AtomicInteger();
    final AtomicInteger callCount3 = new AtomicInteger();

    Spritz.addUncaughtErrorHandler( e -> callCount1.incrementAndGet() );
    Spritz.addUncaughtErrorHandler( e -> callCount2.incrementAndGet() );
    Spritz.addUncaughtErrorHandler( e -> callCount3.incrementAndGet() );

    Spritz.reportUncaughtError( throwable );

    assertEquals( callCount1.get(), 1 );
    assertEquals( callCount2.get(), 1 );
    assertEquals( callCount3.get(), 1 );

    Spritz.reportUncaughtError( throwable );

    assertEquals( callCount1.get(), 2 );
    assertEquals( callCount2.get(), 2 );
    assertEquals( callCount3.get(), 2 );
  }

  @Test
  public void onUncaughtError_whereOneHandlerGeneratesError()
  {
    final Stream<Object> stream = Stream.empty();
    final Throwable throwable = new IllegalStateException();

    final AtomicInteger callCount1 = new AtomicInteger();
    final AtomicInteger callCount3 = new AtomicInteger();

    final RuntimeException exception = new RuntimeException( "X" );

    final UncaughtErrorHandler handler2 = e -> {
      throw exception;
    };
    Spritz.addUncaughtErrorHandler( e -> callCount1.incrementAndGet() );
    Spritz.addUncaughtErrorHandler( handler2 );
    Spritz.addUncaughtErrorHandler( e -> callCount3.incrementAndGet() );

    Spritz.reportUncaughtError( throwable );

    assertEquals( callCount1.get(), 1 );
    assertEquals( callCount3.get(), 1 );

    final ArrayList<TestLogger.LogEntry> entries = getTestLogger().getEntries();
    assertEquals( entries.size(), 1 );
    final TestLogger.LogEntry entry1 = entries.get( 0 );
    assertEquals( entry1.getMessage(),
                  "Exception when notifying error handler '" + handler2 + "' of '" + throwable + "' error." );
    assertEquals( entry1.getThrowable(), exception );

    Spritz.reportUncaughtError( throwable );

    assertEquals( callCount1.get(), 2 );
    assertEquals( callCount3.get(), 2 );

    assertEquals( getTestLogger().getEntries().size(), 2 );
  }

  @Test
  public void onUncaughtError_whereOneHandlerGeneratesError_but_Spritz_areNamesEnabled_is_false()
  {
    SpritzTestUtil.disableNames();

    final Throwable throwable = new IllegalStateException();

    final RuntimeException exception = new RuntimeException( "X" );

    final UncaughtErrorHandler handler2 = e -> {
      throw exception;
    };
    Spritz.addUncaughtErrorHandler( handler2 );

    Spritz.reportUncaughtError( throwable );

    final ArrayList<TestLogger.LogEntry> entries = getTestLogger().getEntries();
    assertEquals( entries.size(), 1 );
    final TestLogger.LogEntry entry1 = entries.get( 0 );
    assertEquals( entry1.getMessage(), "Error triggered when invoking UncaughtErrorHandler.onUncaughtError()" );
    assertEquals( entry1.getThrowable(), exception );

    Spritz.reportUncaughtError( throwable );

    assertEquals( getTestLogger().getEntries().size(), 2 );
  }

  @Test
  public void addUncaughtErrorHandler_errorHandlersDisabled()
  {
    SpritzTestUtil.disableUncaughtErrorHandlers();

    final UncaughtErrorHandler handler = e -> {
    };

    assertInvariantFailure( () -> Spritz.addUncaughtErrorHandler( handler ),
                            "Spritz-0182: UncaughtErrorHandlerSupport.get() invoked when Spritz.areUncaughtErrorHandlersEnabled() returns false." );

    // This should produce no error and will be silently omitted
    Spritz.reportUncaughtError( new IllegalStateException() );
  }
}
