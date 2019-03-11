package spritz.dom;

import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class WebSocketEvent
{
  @Nonnull
  private final WebSocket _webSocket;

  public WebSocketEvent( @Nonnull final WebSocket webSocket )
  {
    _webSocket = Objects.requireNonNull(webSocket);
  }

  @Nonnull
  public WebSocket getWebSocket()
  {
    return _webSocket;
  }
}
