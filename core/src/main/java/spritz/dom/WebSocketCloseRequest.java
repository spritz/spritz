package spritz.dom;

import javax.annotation.Nullable;

public final class WebSocketCloseRequest
  extends WebSocketRequest
{
  /**
   * The error code used to close the socket. The browser allows codes 1000 (for success) and 3000-4999 with specific
   * meanings as described in the <a href="https://tools.ietf.org/html/rfc6455#section-7.4.1">Reserved Status Code Ranges</a>
   * section describing the protocol
   */
  private final int _code;
  /**
   * The reason associated with the close if any.
   */
  @Nullable
  private final String _reason;

  public WebSocketCloseRequest()
  {
    this( 1000 );
  }

  public WebSocketCloseRequest( final int code )
  {
    this( code, null );
  }

  public WebSocketCloseRequest( final int code, @Nullable final String reason )
  {
    assert 1000 == code || ( code >= 3000 && code <= 4999 );
    _code = code;
    _reason = reason;
  }

  public int getCode()
  {
    return _code;
  }

  @Nullable
  public String getReason()
  {
    return _reason;
  }
}
