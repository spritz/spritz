package spritz.dom;

import akasha.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class WebSocketResponse
{
  @Nonnull
  private final WebSocket _webSocket;

  public WebSocketResponse( @Nonnull final WebSocket webSocket )
  {
    _webSocket = Objects.requireNonNull( webSocket );
  }

  @Nonnull
  public WebSocket getWebSocket()
  {
    return _webSocket;
  }
}
