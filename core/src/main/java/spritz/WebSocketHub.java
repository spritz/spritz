package spritz;

import akasha.WebSocket;
import java.util.ArrayList;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.dom.WebSocketArrayBufferMessageRequest;
import spritz.dom.WebSocketArrayBufferViewMessageRequest;
import spritz.dom.WebSocketBlobMessageRequest;
import spritz.dom.WebSocketCloseCompleted;
import spritz.dom.WebSocketCloseException;
import spritz.dom.WebSocketCloseRequest;
import spritz.dom.WebSocketConfig;
import spritz.dom.WebSocketOpenCompleted;
import spritz.dom.WebSocketRequest;
import spritz.dom.WebSocketResponse;
import spritz.dom.WebSocketStringMessageRequest;

public final class WebSocketHub
  extends Hub<WebSocketRequest, WebSocketResponse>
{
  private static final int DEFAULT_ERROR_CODE = 4000;
  @Nonnull
  private final WebSocketConfig _config;
  @Nullable
  private ArrayList<WebSocketRequest> _input;
  @Nullable
  private WebSocket _webSocket;
  @Nullable
  private ForwardToHubDownstreamSubscriber<WebSocketResponse> _subscriber;

  WebSocketHub( @Nullable final String name, @Nonnull final WebSocketConfig config )
  {
    super( Spritz.areNamesEnabled() ? Stream.generateName( name, "webSocket" ) : null );
    _config = Objects.requireNonNull( config );
  }

  @Override
  void completeSubscribe( @Nonnull final DownstreamSubscription subscription )
  {
    if ( null == _subscriber )
    {
      final WebSocketSource source =
        new WebSocketSource( Spritz.areNamesEnabled() ? getName() + ".source()" : null, _config );
      _subscriber = new ForwardToHubDownstreamSubscriber<>( this );
      source.subscribe( _subscriber );
    }
  }

  @Override
  void performNext( @Nonnull final WebSocketRequest item )
  {
    if ( null == _webSocket )
    {
      bufferRequest( item );
    }
    else if ( item instanceof WebSocketStringMessageRequest )
    {
      _webSocket.send( ( (WebSocketStringMessageRequest) item ).getData() );
    }
    else if ( item instanceof WebSocketArrayBufferMessageRequest )
    {
      _webSocket.send( ( (WebSocketArrayBufferMessageRequest) item ).getData() );
    }
    else if ( item instanceof WebSocketArrayBufferViewMessageRequest )
    {
      _webSocket.send( ( (WebSocketArrayBufferViewMessageRequest) item ).getData() );
    }
    else if ( item instanceof WebSocketBlobMessageRequest )
    {
      _webSocket.send( ( (WebSocketBlobMessageRequest) item ).getData() );
    }
    else
    {
      assert item instanceof WebSocketCloseRequest;
      final WebSocketCloseRequest closeRequest = (WebSocketCloseRequest) item;
      close( closeRequest.getCode(), closeRequest.getReason() );
    }
  }

  @Override
  void performError( @Nonnull final Throwable error )
  {
    if ( null != _webSocket )
    {
      if ( error instanceof WebSocketCloseException )
      {
        final WebSocketCloseException e = (WebSocketCloseException) error;
        close( e.getCode(), e.getMessage() );
      }
      else
      {
        close( DEFAULT_ERROR_CODE, null );
      }
    }
    else
    {
      if ( error instanceof WebSocketCloseException )
      {
        final WebSocketCloseException e = (WebSocketCloseException) error;
        bufferRequest( new WebSocketCloseRequest( e.getCode(), e.getMessage() ) );
      }
      else
      {
        bufferRequest( new WebSocketCloseRequest( DEFAULT_ERROR_CODE ) );
      }
    }
  }

  @Override
  void performComplete()
  {
    if ( null != _webSocket )
    {
      _webSocket.close();
    }
    else if ( isNotDone() )
    {
      bufferRequest( new WebSocketCloseRequest() );
    }
  }

  @Override
  void downstreamNext( @Nonnull final WebSocketResponse item )
  {
    super.downstreamNext( item );
    if ( item instanceof WebSocketOpenCompleted )
    {
      _webSocket = item.getWebSocket();
      if ( null != _input )
      {
        for ( final WebSocketRequest request : _input )
        {
          if ( isDone() )
          {
            break;
          }
          else
          {
            performNext( request );
          }
        }
        _input = null;
      }
    }
    else if ( item instanceof WebSocketCloseCompleted )
    {
      _webSocket = null;
    }
  }

  @Override
  void downstreamError( @Nonnull final Throwable error )
  {
    super.downstreamError( error );
    terminateUpstreamSubscribers();
  }

  @Override
  void downstreamComplete()
  {
    super.downstreamComplete();
    terminateUpstreamSubscribers();
  }

  @Override
  void terminateUpstreamSubscribers()
  {
    super.terminateUpstreamSubscribers();
    if ( null != _subscriber )
    {
      _subscriber.cancel();
    }
  }

  private void bufferRequest( @Nonnull final WebSocketRequest item )
  {
    assert isNotDone();
    assert null == _webSocket;
    if ( null == _input )
    {
      _input = new ArrayList<>();
    }
    _input.add( item );
  }

  private void close( final int code, @Nullable final String reason )
  {
    assert null != _webSocket;
    if ( null == reason )
    {
      _webSocket.close( code );
    }
    else
    {
      _webSocket.close( code, reason );
    }
  }
}
