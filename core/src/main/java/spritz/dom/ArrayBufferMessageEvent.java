package spritz.dom;

import elemental2.core.ArrayBuffer;
import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ArrayBufferMessageEvent
  extends WebSocketEvent
{
  @Nonnull
  private final ArrayBuffer _data;

  public ArrayBufferMessageEvent( @Nonnull final WebSocket webSocket, @Nonnull final ArrayBuffer data )
  {
    super( webSocket );
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public ArrayBuffer getData()
  {
    return _data;
  }
}

