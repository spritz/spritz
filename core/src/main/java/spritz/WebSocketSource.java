package spritz;

import elemental2.dom.Event;
import elemental2.dom.MessageEvent;
import elemental2.dom.WebSocket;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import jsinterop.base.Js;
import spritz.dom.WebSocketArrayBufferMessage;
import spritz.dom.WebSocketBlobMessage;
import spritz.dom.WebSocketCloseCompleted;
import spritz.dom.WebSocketConfig;
import spritz.dom.WebSocketErrorException;
import spritz.dom.WebSocketOpenCompleted;
import spritz.dom.WebSocketResponse;
import spritz.dom.WebSocketStringMessage;
import spritz.dom.util.CloseEvent;
import spritz.dom.util.EventMessageEventTypeParameterUnionType;
import spritz.dom.util.OnerrorFn;

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
      //TODO: Remove Js.uncheckedCast with next version of Elemental2 and possibly detect single value in array variant
      _webSocket = null == protocols ? new WebSocket( url ) : new WebSocket( url, Js.uncheckedCast( protocols ) );
      final String binaryType = config.getBinaryType();
      if ( null != binaryType )
      {
        _webSocket.binaryType = binaryType;
      }
      //TODO: Use real property when supported by underlying Elemental2
      final OnerrorFn onError = this::onWebSocketError;
      Js.asPropertyMap( _webSocket ).set( "onerror", onError );
      //TODO: Remove Js.uncheckedCast( e ) after next release of elemental2
      _webSocket.onmessage = e -> onWebSocketMessage( Js.uncheckedCast( e ) );
      _webSocket.onopen = e -> {
        onWebSocketOpen();
        return null;
      };
      _webSocket.onclose = e -> {
        onWebSocketClose( (CloseEvent) e );
        //TODO: Remove return after Elemental2 update
        return null;
      };
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

    private void onWebSocketMessage( @Nonnull final MessageEvent<EventMessageEventTypeParameterUnionType> event )
    {
      if ( isDone() )
      {
        // The subscription has been cancelled before the message received
        if ( WebSocket.OPEN == _webSocket.readyState || WebSocket.CONNECTING == _webSocket.readyState )
        {
          _webSocket.close();
        }
      }
      else
      {
        final EventMessageEventTypeParameterUnionType data = event.data;
        if ( data.isString() )
        {
          doNext( new WebSocketStringMessage( _webSocket, data.asString() ) );
        }
        else if ( data.isArrayBuffer() )
        {
          doNext( new WebSocketArrayBufferMessage( _webSocket, data.asArrayBuffer() ) );
        }
        else
        {
          assert data.isBlob();
          doNext( new WebSocketBlobMessage( _webSocket, data.asBlob() ) );
        }
      }
    }

    private void onWebSocketClose( @Nonnull final CloseEvent event )
    {
      if ( isNotDone() )
      {
        doNext( new WebSocketCloseCompleted( _webSocket, event.code, event.reason, event.wasClean ) );
        if ( event.wasClean )
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
        if ( WebSocket.OPEN == _webSocket.readyState || WebSocket.CONNECTING == _webSocket.readyState )
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
      getSubscriber().onNext( item );
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
