package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

public final class ConnectableStream<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Subject<T> _subject;
  private boolean _connectCalled;

  ConnectableStream( @Nullable final String name, @Nonnull final Stream<T> upstream, @Nonnull final Subject<T> subject )
  {
    super( name, upstream );
    _subject = Objects.requireNonNull( subject );
  }

  @Override
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _subject.subscribe( subscriber );
  }

  /**
   * Ensure that {@link #connect()} is when there is a first downstream subscriber and {@link #disconnect()} when the last downstream subscriber is removed.
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
   * Ensure that {@link #connect()} is when there is a first downstream subscriber and {@link #disconnect()} when the last downstream subscriber is removed.
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
      apiInvariant( () -> !_connectCalled,
                    () -> "Spritz-0033: Subject.connect(...) invoked on subject '" + getName() + "' but " +
                          "subject is already connected." );
      _connectCalled = true;
    }
    getUpstream().subscribe( _subject.newUpstreamSubscriber() );
  }

  public void disconnect()
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      apiInvariant( () -> _connectCalled,
                    () -> "Spritz-1033: Subject.disconnect(...) invoked on subject '" + getName() + "' but " +
                          "subject is not connected." );
      _connectCalled = false;
    }
    _subject.terminateUpstreamSubscribers();
  }

  @Nonnull
  Subject<T> getSubject()
  {
    return _subject;
  }
}
