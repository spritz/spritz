package spritz;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class TestLogger
  implements SpritzTestUtil.Logger
{
  public static final class LogEntry
  {
    @Nonnull
    private final String _message;
    @Nullable
    private final Throwable _throwable;

    LogEntry( @Nonnull final String message, @Nullable final Throwable throwable )
    {
      _message = message;
      _throwable = throwable;
    }

    @Nonnull
    public String getMessage()
    {
      return _message;
    }

    @Nullable
    public Throwable getThrowable()
    {
      return _throwable;
    }
  }

  private final ArrayList<LogEntry> _entries = new ArrayList<>();

  @Override
  public void log( @Nonnull final String message, @Nullable final Throwable throwable )
  {
    _entries.add( new LogEntry( message, throwable ) );
  }

  @Nonnull
  public ArrayList<LogEntry> getEntries()
  {
    return _entries;
  }
}
