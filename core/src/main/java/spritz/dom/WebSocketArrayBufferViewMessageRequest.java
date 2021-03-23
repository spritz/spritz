package spritz.dom;

import akasha.core.ArrayBufferView;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketArrayBufferViewMessageRequest
  extends WebSocketRequest
{
  /**
   * The data to transmit.
   */
  @Nonnull
  private final ArrayBufferView _data;

  public WebSocketArrayBufferViewMessageRequest( @Nonnull final ArrayBufferView data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public ArrayBufferView getData()
  {
    return _data;
  }
}
