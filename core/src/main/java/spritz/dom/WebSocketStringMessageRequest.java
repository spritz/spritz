package spritz.dom;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketStringMessageRequest
  extends WebSocketRequest
{
  /**
   * The data to transmit.
   */
  @Nonnull
  private final String _data;

  public WebSocketStringMessageRequest( @Nonnull final String data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public String getData()
  {
    return _data;
  }
}
