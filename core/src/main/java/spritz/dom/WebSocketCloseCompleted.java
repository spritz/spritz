package spritz.dom;

import akasha.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WebSocketCloseCompleted
  extends WebSocketResponse
{
  private final int _code;
  @Nonnull
  private final String _reason;
  private final boolean _wasClean;

  public WebSocketCloseCompleted( @Nonnull final WebSocket webSocket,
                                  final int code,
                                  @Nonnull final String reason,
                                  final boolean wasClean )
  {
    super( webSocket );
    _code = code;
    _reason = Objects.requireNonNull( reason );
    _wasClean = wasClean;
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

  public boolean wasClean()
  {
    return _wasClean;
  }
}
