package spritz.dom;

import elemental2.dom.WebSocket;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FailureEvent
  extends WebSocketEvent
{
  @Nullable
  private final Throwable _error;

  public FailureEvent( @Nonnull final WebSocket webSocket, @Nullable final Throwable error )
  {
    super( webSocket );
    _error = error;
  }

  @Nullable
  public Throwable getError()
  {
    return _error;
  }
}
