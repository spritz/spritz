package spritz.dom;

import elemental2.dom.Blob;
import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class StringMessageEvent
  extends WebSocketEvent
{
  @Nonnull
  private final String _data;

  public StringMessageEvent( @Nonnull final WebSocket webSocket, @Nonnull final String data )
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
