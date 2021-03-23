package spritz.dom;

import akasha.Blob;
import akasha.core.ArrayBuffer;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketArrayBufferMessageRequest
  extends WebSocketRequest
{
  /**
   * The data to transmit.
   */
  @Nonnull
  private final ArrayBuffer _data;

  public WebSocketArrayBufferMessageRequest( @Nonnull final ArrayBuffer data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public ArrayBuffer getData()
  {
    return _data;
  }
}
