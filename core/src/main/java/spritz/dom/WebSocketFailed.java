package spritz.dom;

import akasha.WebSocket;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class WebSocketFailed
  extends WebSocketResponse
{
  @Nullable
  private final Throwable _error;

  public WebSocketFailed( @Nonnull final WebSocket webSocket, @Nullable final Throwable error )
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
