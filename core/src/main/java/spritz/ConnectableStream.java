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
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _subject.subscribe( subscriber );
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
}
