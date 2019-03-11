package spritz.dom;

import elemental2.dom.Blob;
import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class BlobMessageEvent
  extends WebSocketEvent
{
  @Nonnull
  private final Blob _data;

  public BlobMessageEvent( @Nonnull final WebSocket webSocket, @Nonnull final Blob data )
  {
    super( webSocket );
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public Blob getData()
  {
    return _data;
  }
}
