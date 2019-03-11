package spritz.dom;

import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class ClosingEvent
  extends WebSocketEvent
{
  private final int _code;
  @Nonnull
  private final String _reason;

  public ClosingEvent( @Nonnull final WebSocket webSocket, final int code, @Nonnull final String reason )
  {
    super( webSocket );
    _code = code;
    _reason = Objects.requireNonNull( reason );
  }

  public int getCode()
  {
    return _code;
  }

  @Nonnull
  public String getReason()
  {
    return _reason;
  }
}
