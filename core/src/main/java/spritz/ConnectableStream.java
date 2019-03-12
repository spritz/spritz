package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

@MetaDataSource
public final class ConnectableStream<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Hub<T,T> _hub;
  private boolean _connected;

  ConnectableStream( @Nullable final String name, @Nonnull final Stream<T> upstream, @Nonnull final Hub<T,T> hub )
  {
    super( name, upstream );
    _hub = Objects.requireNonNull( hub );
  }

  @Override
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _hub.subscribe( subscriber );
  }

  /**
   * a stream that ensures that as long as there is at least one subscriber to this stream, then this stream will be subscribed to upstream.
   *
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> refCount()
  {
    return refCount( null );
  }

  /**
   * Return a stream that ensures that as long as there is at least one subscriber to this stream, then this stream will be subscribed to upstream.
   *
   * @param name the name specified by the user.
   * @return the new stream.
   */
  @Nonnull
  @DocCategory( DocCategory.Type.UNKNOWN )
  public final Stream<T> refCount( @Nullable final String name )
  {
    return compose( s -> new RefCountOperator<>( name, (ConnectableStream<T>) s ) );
  }

  public void connect()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      apiInvariant( () -> !_connected,
                    () -> "Spritz-0033: ConnectableStream.connect(...) invoked on subject '" + getName() + "' but " +
                          "subject is already connected." );
    }
    _connected = true;
    getUpstream().subscribe( _hub.newUpstreamSubscriber() );
  }

  public void disconnect()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      apiInvariant( () -> _connected,
                    () -> "Spritz-1033: Subject.disconnect(...) invoked on subject '" + getName() + "' but " +
                          "subject is not connected." );
    }
    _connected = false;
    _hub.terminateUpstreamSubscribers();
  }

  /**
   * Return true if stream is connected, false otherwise.
   *
   * @return true if stream is connected, false otherwise.
   */
  public boolean isConnected()
  {
    return _connected;
  }

  @Nonnull
  Hub<T,T> getHub()
  {
    return _hub;
  }
}
