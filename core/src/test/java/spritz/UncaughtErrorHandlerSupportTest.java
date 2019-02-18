package spritz;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class UncaughtErrorHandlerSupportTest
  extends AbstractSpritzTest
{
  @Test
  public void basicOperation()
  {
    final UncaughtErrorHandlerSupport support = UncaughtErrorHandlerSupport.get();

    final Stream<Object> stream = Stream.empty();
    final Throwable throwable = new IllegalStateException();

    final AtomicInteger callCount = new AtomicInteger();

    final UncaughtErrorHandler handler = ( streamArg, throwableArg ) -> {
      callCount.incrementAndGet();
      assertEquals( streamArg, stream );
      assertEquals( throwableArg, throwable );
    };
    support.addUncaughtErrorHandler( handler );
    assertEquals( support.getErrorHandlers().size(), 1 );

    support.onUncaughtError( stream, throwable );

    assertEquals( callCount.get(), 1 );

    support.onUncaughtError( stream, throwable );

    assertEquals( callCount.get(), 2 );

    support.removeUncaughtErrorHandler( handler );

    assertEquals( support.getErrorHandlers().size(), 0 );

    support.onUncaughtError( stream, throwable );

    // Not called again
    assertEquals( callCount.get(), 2 );
  }

  @Test
  public void addUncaughtErrorHandler_alreadyExists()
  {
    final UncaughtErrorHandlerSupport support = UncaughtErrorHandlerSupport.get();

    final UncaughtErrorHandler handler = ( streamArg, throwableArg ) -> {
    };
    support.addUncaughtErrorHandler( handler );

    assertInvariantFailure( () -> support.addUncaughtErrorHandler( handler ),
                            "Spritz-0096: Attempting to add handler " + handler + " that is already in " +
                            "the list of error handlers." );
  }

  @Test
  public void removeUncaughtErrorHandler_noExists()
  {
    final UncaughtErrorHandlerSupport support = UncaughtErrorHandlerSupport.get();

    final UncaughtErrorHandler handler = ( streamArg, throwableArg ) -> {
    };

    assertInvariantFailure( () -> support.removeUncaughtErrorHandler( handler ),
                            "Spritz-0097: Attempting to remove handler " + handler + " that is not in " +
                            "the list of error handlers." );
  }

  @Test
  public void multipleHandlers()
  {
    final UncaughtErrorHandlerSupport support = UncaughtErrorHandlerSupport.get();

    final Stream<Object> stream = Stream.empty();
    final Throwable throwable = new IllegalStateException();

    final AtomicInteger callCount1 = new AtomicInteger();
    final AtomicInteger callCount2 = new AtomicInteger();
    final AtomicInteger callCount3 = new AtomicInteger();

    final UncaughtErrorHandler handler1 = ( streamArg, throwableArg ) -> callCount1.incrementAndGet();
    final UncaughtErrorHandler handler2 = ( streamArg, throwableArg ) -> callCount2.incrementAndGet();
    final UncaughtErrorHandler handler3 = ( streamArg, throwableArg ) -> callCount3.incrementAndGet();
    support.addUncaughtErrorHandler( handler1 );
    support.addUncaughtErrorHandler( handler2 );
    support.addUncaughtErrorHandler( handler3 );

    assertEquals( support.getErrorHandlers().size(), 3 );

    support.onUncaughtError( stream, throwable );

    assertEquals( callCount1.get(), 1 );
    assertEquals( callCount2.get(), 1 );
    assertEquals( callCount3.get(), 1 );

    support.onUncaughtError( stream, throwable );

    assertEquals( callCount1.get(), 2 );
    assertEquals( callCount2.get(), 2 );
    assertEquals( callCount3.get(), 2 );
  }

  @Test
  public void onUncaughtError_whereOneHandlerGeneratesError()
  {
    final UncaughtErrorHandlerSupport support = UncaughtErrorHandlerSupport.get();

    final Stream<Object> stream = Stream.empty();
    final Throwable throwable = new IllegalStateException();

    final AtomicInteger callCount1 = new AtomicInteger();
    final AtomicInteger callCount3 = new AtomicInteger();

    final RuntimeException exception = new RuntimeException( "X" );

    final UncaughtErrorHandler handler1 = ( streamArg, throwableArg ) -> callCount1.incrementAndGet();
    final UncaughtErrorHandler handler2 = ( streamArg, throwableArg ) -> {
      throw exception;
    };
    final UncaughtErrorHandler handler3 = ( streamArg, throwableArg ) -> callCount3.incrementAndGet();
    support.addUncaughtErrorHandler( handler1 );
    support.addUncaughtErrorHandler( handler2 );
    support.addUncaughtErrorHandler( handler3 );

    support.onUncaughtError( stream, throwable );

    assertEquals( callCount1.get(), 1 );
    assertEquals( callCount3.get(), 1 );

    final ArrayList<TestLogger.LogEntry> entries = getTestLogger().getEntries();
    assertEquals( entries.size(), 1 );
    final TestLogger.LogEntry entry1 = entries.get( 0 );
    assertEquals( entry1.getMessage(),
                  "Exception when notifying error handler '" + handler2 + "' of '" +
                  throwable + "' error in stream '" + stream + "'." );
    assertEquals( entry1.getThrowable(), exception );

    support.onUncaughtError( stream, throwable );

    assertEquals( callCount1.get(), 2 );
    assertEquals( callCount3.get(), 2 );

    assertEquals( getTestLogger().getEntries().size(), 2 );
  }

  @Test
  public void onUncaughtError_whereOneHandlerGeneratesError_but_Spritz_areNamesEnabled_is_false()
  {
    SpritzTestUtil.disableNames();

    final UncaughtErrorHandlerSupport support = UncaughtErrorHandlerSupport.get();

    final Stream<Object> stream = Stream.empty();
    final Throwable throwable = new IllegalStateException();

    final RuntimeException exception = new RuntimeException( "X" );

    final UncaughtErrorHandler handler2 = ( streamArg, throwableArg ) -> {
      throw exception;
    };
    support.addUncaughtErrorHandler( handler2 );

    support.onUncaughtError( stream, throwable );

    final ArrayList<TestLogger.LogEntry> entries = getTestLogger().getEntries();
    assertEquals( entries.size(), 1 );
    final TestLogger.LogEntry entry1 = entries.get( 0 );
    assertEquals( entry1.getMessage(), "Error triggered when invoking UncaughtErrorHandler.onUncaughtError()" );
    assertEquals( entry1.getThrowable(), exception );

    support.onUncaughtError( stream, throwable );

    assertEquals( getTestLogger().getEntries().size(), 2 );
  }
}
