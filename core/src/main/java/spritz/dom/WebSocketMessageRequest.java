package spritz.dom;

import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketMessageRequest
  extends WebSocketRequest
{
  /**
   * The data to transmit.
   */
  @Nonnull
  private final WebSocket.SendDataUnionType _data;

  public WebSocketMessageRequest( @Nonnull final WebSocket.SendDataUnionType data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public WebSocket.SendDataUnionType getData()
  {
    return _data;
  }
}
