package spritz.dom;

import akasha.Blob;
import akasha.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketBlobMessage
  extends WebSocketResponse
{
  @Nonnull
  private final Blob _data;

  public WebSocketBlobMessage( @Nonnull final WebSocket webSocket, @Nonnull final Blob data )
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
