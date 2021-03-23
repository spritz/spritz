package spritz.examples.dom.web_socket;

import akasha.BinaryType;
import akasha.Console;
import akasha.Global;
import akasha.core.ArrayBuffer;
import com.google.gwt.core.client.EntryPoint;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import spritz.Stream;
import spritz.Subscriber;
import spritz.Subscription;
import spritz.WebSocketHub;
import spritz.dom.WebSocketArrayBufferMessageRequest;
import spritz.dom.WebSocketCloseRequest;
import spritz.dom.WebSocketConfig;
import spritz.dom.WebSocketRequest;
import spritz.dom.WebSocketResponse;
import spritz.dom.WebSocketStringMessageRequest;
import zemeckis.Zemeckis;

public final class WebSocketExample
  implements EntryPoint
{
  @Override
  public void onModuleLoad()
  {
    final WebSocketHub hub =
      Stream.webSocket( new WebSocketConfig( "wss://echo.websocket.org", null, BinaryType.arraybuffer ) );

    Console.log( "Construction complete" );
    final LoggingSubscriber<WebSocketResponse> subscriber = new LoggingSubscriber<>( "WS" );
    hub.subscribe( subscriber );
    hub.next( new WebSocketStringMessageRequest( "Hello" ) );
    hub.next( new WebSocketArrayBufferMessageRequest( new ArrayBuffer( 22 ) ) );
    hub.next( new WebSocketStringMessageRequest( "Hello" ) );

    Stream.<WebSocketRequest>create( s -> {
      s.next( new WebSocketStringMessageRequest( "ABC" ) );
      s.next( new WebSocketStringMessageRequest( "def" ) );
      s.next( new WebSocketStringMessageRequest( "GHI" ) );
      // Never completes ....
    } ).subscribe( hub );

    Global.setTimeout( () -> {
      Console.log( "PreCancel" );
      subscriber.getSubscription().cancel();
      Console.log( "PostCancel" );
    }, 2000 );

    Global.setTimeout( () -> hub.next( new WebSocketStringMessageRequest( "Hello" ) ), 3000 );
    Global.setTimeout( () -> hub.subscribe( subscriber ), 3500 );
    Global.setTimeout( () -> hub.next( new WebSocketCloseRequest( 3000, "Hello" ) ), 4000 );
    Global.setTimeout( hub::complete, 5000 );

    Console.log( "Subscribe complete" );
  }

  static final class LoggingSubscriber<T>
    implements Subscriber<T>
  {
    @Nullable
    private final String _prefix;
    private Subscription _subscription;

    LoggingSubscriber( @Nullable final String prefix )
    {
      _prefix = prefix;
    }

    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      Console.log( prefix() + "onSubscribe(" + subscription + ")" + suffix() );
      _subscription = subscription;
    }

    @Override
    public void onItem( @Nonnull final T item )
    {
      Console.log( prefix() + "onItem(" + item + ")" + suffix() );
    }

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      Console.log( prefix() + "onError(" + error + ")" + suffix() );
    }

    @Override
    public void onComplete()
    {
      Console.log( prefix() + "onComplete()" + suffix() );
    }

    @Nonnull
    private String prefix()
    {
      return null == _prefix ? "" : _prefix + ":";
    }

    @Nonnull
    private String suffix()
    {
      return " on " + Objects.requireNonNull( Zemeckis.currentVpu() ).getName();
    }

    Subscription getSubscription()
    {
      return _subscription;
    }
  }
}
