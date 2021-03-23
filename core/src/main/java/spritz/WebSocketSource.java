package spritz;

import akasha.Blob;
import akasha.CloseEvent;
import akasha.Event;
import akasha.MessageEvent;
import akasha.WebSocket;
import akasha.core.ArrayBuffer;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.dom.WebSocketArrayBufferMessage;
import spritz.dom.WebSocketBlobMessage;
import spritz.dom.WebSocketCloseCompleted;
import spritz.dom.WebSocketConfig;
import spritz.dom.WebSocketErrorException;
import spritz.dom.WebSocketOpenCompleted;
import spritz.dom.WebSocketResponse;
import spritz.dom.WebSocketStringMessage;

final class WebSocketSource
  extends Stream<WebSocketResponse>
{
  @Nonnull
  private final WebSocketConfig _config;

  WebSocketSource( @Nullable final String name, @Nonnull final WebSocketConfig config )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "webSocket", config.getUrl() ) : null );
    _config = Objects.requireNonNull( config );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super WebSocketResponse> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.connect();
    return subscription;
  }

  private static final class WorkerSubscription
    extends AbstractStreamSubscription<WebSocketResponse, WebSocketSource>
  {
    private WebSocket _webSocket;

    WorkerSubscription( @Nonnull final WebSocketSource stream,
                        @Nonnull final Subscriber<? super WebSocketResponse> subscriber )
    {
      super( stream, subscriber );
    }

    void connect()
    {
      final WebSocketConfig config = getStream()._config;
      final String url = config.getUrl();
      final String[] protocols = config.getProtocols();
      _webSocket = null == protocols ? new WebSocket( url ) : new WebSocket( url, protocols );
      final String binaryType = config.getBinaryType();
      if ( null != binaryType )
      {
        _webSocket.binaryType = binaryType;
      }
      _webSocket.onerror = this::onWebSocketError;
      _webSocket.onmessage = this::onWebSocketMessage;
      _webSocket.onopen = e -> onWebSocketOpen();
      _webSocket.onclose = this::onWebSocketClose;
    }

    private void onWebSocketOpen()
    {
      if ( isDone() )
      {
        // The subscription has been cancelled before the connection completed
        _webSocket.close();
      }
      else
      {
        doNext( new WebSocketOpenCompleted( _webSocket ) );
      }
    }

    private void onWebSocketMessage( @Nonnull final MessageEvent event )
    {
      if ( isDone() )
      {
        // The subscription has been cancelled before the message received
        if ( WebSocket.OPEN == _webSocket.readyState() || WebSocket.CONNECTING == _webSocket.readyState() )
        {
          _webSocket.close();
        }
      }
      else
      {
        final Object data = event.data();
        //noinspection ConstantConditions
        if ( data instanceof String )
        {
          doNext( new WebSocketStringMessage( _webSocket, (String)data ) );
        }
        else if ( data instanceof ArrayBuffer )
        {
          doNext( new WebSocketArrayBufferMessage( _webSocket, (ArrayBuffer) data ) );
        }
        else
        {
          assert data instanceof Blob;
          doNext( new WebSocketBlobMessage( _webSocket, (Blob)data ) );
        }
      }
    }

    private void onWebSocketClose( @Nonnull final CloseEvent event )
    {
      if ( isNotDone() )
      {
        doNext( new WebSocketCloseCompleted( _webSocket, event.code(), event.reason(), event.wasClean() ) );
        if ( event.wasClean() )
        {
          onComplete();
        }
        else
        {
          onError( new WebSocketErrorException( event ) );
        }
      }
    }

    private void onWebSocketError( @Nonnull final Event event )
    {
      if ( isDone() )
      {
        // The subscription has been cancelled before the error received
        final int readyState = _webSocket.readyState();
        if ( WebSocket.OPEN == readyState || WebSocket.CONNECTING == readyState )
        {
          _webSocket.close();
        }
      }
      else
      {
        onError( new WebSocketErrorException( event ) );
      }
    }

    private void doNext( @Nonnull final WebSocketResponse item )
    {
      getSubscriber().onItem( item );
    }

    private void onError( @Nonnull final Throwable error )
    {
      getSubscriber().onError( error );
    }

    private void onComplete()
    {
      getSubscriber().onComplete();
    }
  }
}
