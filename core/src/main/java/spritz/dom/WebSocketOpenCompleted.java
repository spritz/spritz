package spritz.dom;

import akasha.WebSocket;
import javax.annotation.Nonnull;

public final class WebSocketOpenCompleted
  extends WebSocketResponse
{
  public WebSocketOpenCompleted( @Nonnull final WebSocket webSocket )
  {
    super( webSocket );
  }
}
