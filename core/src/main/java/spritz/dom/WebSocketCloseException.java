package spritz.dom;

import javax.annotation.Nullable;

/**
 * An exception used by WebSocketSubject to close WebSocket with a code and optionally a message.
 * The browser allows codes 1000 (for success) and 3000-4999 with specific meanings as described in
 * the <a href="https://tools.ietf.org/html/rfc6455#section-7.4.1">Reserved Status Code Ranges</a>
 * section describing the protocol. However this exception assumes the close is an error rather than
 * a normal close/complete and thus disallows 1000.
 */
public final class WebSocketCloseException
  extends Exception
{
  /**
   * The error code used to close the socket. Must be 3000-4999.
   */
  private final int _code;

  /**
   * Close the web socket with specified code and message.
   *
   * @param code    the code.
   * @param message the message.
   */
  public WebSocketCloseException( @Nullable final String message, final int code )
  {
    super( message );
    assert code >= 3000 && code <= 4999;
    _code = code;
  }

  /**
   * Close the web socket with specified code.
   *
   * @param code the code.
   */
  public WebSocketCloseException( final int code )
  {
    this( null, code );
  }

  /**
   * Return the error code used to close Websocket.
   *
   * @return the error code used to close Websocket.
   */
  public int getCode()
  {
    return _code;
  }
}
