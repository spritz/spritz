package spritz.dom;

import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class OpenedEvent
  extends WebSocketEvent
{
  public OpenedEvent( @Nonnull final WebSocket webSocket )
  {
    super( webSocket );
  }
}
