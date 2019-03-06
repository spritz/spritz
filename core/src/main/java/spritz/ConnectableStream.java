package spritz;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

public final class ConnectableStream<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Supplier<Subject<T>> _subjectFactory;
  @Nullable
  private Subject<T> _subject;
  @Nullable
  private Subscription _upstreamSubscription;
  private boolean _connectCalled;

  ConnectableStream( @Nullable final String name,
                     @Nonnull final Stream<T> upstream,
                     @Nonnull final Supplier<Subject<T>> subjectFactory )
  {
    super( name, upstream );
    _subjectFactory = Objects.requireNonNull( subjectFactory );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getSubject().subscribe( subscriber );
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
    getUpstream().subscribe( new Subscriber<T>()
    {
      @Override
      public void onSubscribe( @Nonnull final Subscription subscription )
      {
        _upstreamSubscription = subscription;
      }

      @Override
      public void onNext( @Nonnull final T item )
      {
        getSubject().doNext( item );
      }

      @Override
      public void onError( @Nonnull final Throwable error )
      {
        getSubject().doError( error );
      }

      @Override
      public void onComplete()
      {
        getSubject().doComplete();
        shutdown( false );
      }
    } );
  }

  public void disconnect()
  {
    shutdown( true );
  }

  private void shutdown( final boolean cancelUpstream )
  {
    if ( Spritz.shouldCheckInvariants() )
    {
      apiInvariant( () -> _connectCalled,
                    () -> "Spritz-0034: Subject.disconnect(...) invoked on subject '" + getName() + "' but " +
                          "subject is not connected." );
      _connectCalled = false;
    }
    assert null != _upstreamSubscription;
    assert null != _subject;
    if ( cancelUpstream )
    {
      _upstreamSubscription.cancel();
    }
    _upstreamSubscription = null;
    _subject = null;
  }

  @Nonnull
  final Subject<T> getSubject()
  {
    if ( null == _subject )
    {
      // Create a new subject the first time we attempt to access it
      _subject = _subjectFactory.get();
      if ( Spritz.shouldCheckInvariants() )
      {
        invariant( () -> null != _subject,
                   () -> "Spritz-0036: ConnectableStream.getSubject() on stream '" + getName() +
                         "' attempted to create subject but factory returned a null subject." );
        assert null != _subject;
        invariant( () -> _subject.isTerminated(),
                   () -> "Spritz-0035: ConnectableStream.getSubject() on stream '" + getName() +
                         "' attempted to create subject but factory returned a terminated subject." );
      }
    }
    return _subject;
  }

  final boolean hasUpstreamSubscription()
  {
    return null != _upstreamSubscription;
  }
}
