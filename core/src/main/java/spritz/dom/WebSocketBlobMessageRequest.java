package spritz.dom;

import akasha.Blob;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketBlobMessageRequest
  extends WebSocketRequest
{
  /**
   * The data to transmit.
   */
  @Nonnull
  private final Blob _data;

  public WebSocketBlobMessageRequest( @Nonnull final Blob data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Nonnull
  public Blob getData()
  {
    return _data;
  }
}
