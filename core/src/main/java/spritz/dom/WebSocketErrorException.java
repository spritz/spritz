package spritz.dom;

import akasha.Event;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * An error generated interacting with the WebSocket.
 */
public final class WebSocketErrorException
  extends Exception
{
  /**
   * The underlying error event.
   */
  private final Event _event;

  /**
   * Create exception to wrap underlying error event..
   *
   * @param event the error event.
   */
  public WebSocketErrorException( @Nonnull final Event event )
  {
    _event = Objects.requireNonNull( event );
  }

  /**
   * Return the underlying error event.
   *
   * @return the underlying error event.
   */
  public Event getEvent()
  {
    return _event;
  }
}
