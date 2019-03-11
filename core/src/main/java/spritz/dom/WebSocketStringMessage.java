package spritz.dom;

import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketStringMessage
  extends WebSocketResponse
{
  @Nonnull
  private final String _data;

  public WebSocketStringMessage( @Nonnull final WebSocket webSocket, @Nonnull final String data )
  {
    super( webSocket );
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public String getData()
  {
    return _data;
  }
}
