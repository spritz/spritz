package spritz;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

/**
 * A PublishOperator that emits items to currently subscribed {@link Subscriber} instances and terminal
 * events to current or late {@link Subscriber} instances.
 */
public final class MulticastSubject<T>
  extends AbstractEventEmitter<T>
  implements Subject<T>
{
  /**
   * The upstream stream stage.
   */
  @Nonnull
  private final Publisher<T> _upstream;
  @Nullable
  private Subscription _upstreamSubscription;
  private final Set<Subscriber<? super T>> _downstreamSubscribers = new HashSet<>();

  /**
   * Create a stream with specified upstream.
   *
   * @param upstream the upstream stream.
   */
  public MulticastSubject( @Nonnull final Publisher<T> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  /**
   * Return the upstream stream.
   *
   * @return the upstream stream.
   */
  @Nonnull
  protected final Publisher<T> getUpstream()
  {
    return _upstream;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    Scheduler.current( () -> doSubscribe( subscriber ) );
  }

  private void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !_downstreamSubscribers.contains( subscriber ),
                    () -> "Spritz-0010: Invoked MulticastSubject.subscribe(...) when subscriber is " +
                          "already subscribed." );
    }
    if ( isComplete() )
    {
      subscriber.onComplete();
    }
    else if ( null != getError() )
    {
      subscriber.onError( getError() );
    }
    else
    {
      _downstreamSubscribers.add( subscriber );
      final Subscription subscription = () -> {
        _downstreamSubscribers.remove( subscriber );
        if ( _downstreamSubscribers.isEmpty() )
        {
          disconnect();
        }
      };
      subscriber.onSubscribe( subscription );
      if ( 1 == _downstreamSubscribers.size() )
      {
        connect();
      }
    }
  }

  private synchronized void connect()
  {
    _upstream.subscribe( new Subscriber<T>()
    {
      @Override
      public void onSubscribe( @Nonnull final Subscription subscription )
      {
        _upstreamSubscription = subscription;
      }

      @Override
      public void onNext( @Nonnull final T item )
      {
        doNext( item );
      }

      @Override
      public void onError( @Nonnull final Throwable error )
      {
        doError( error );
      }

      @Override
      public void onComplete()
      {
        doComplete();
      }
    } );
  }

  private synchronized void disconnect()
  {
    assert null != _upstreamSubscription;
    _upstreamSubscription.cancel();
    _upstreamSubscription = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected synchronized void doNext( @Nonnull final T item )
  {
    for ( final Subscriber<? super T> subscriber : _downstreamSubscribers )
    {
      subscriber.onNext( item );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected synchronized void doError( @Nonnull final Throwable error )
  {
    for ( final Subscriber<? super T> subscriber : _downstreamSubscribers )
    {
      subscriber.onError( error );
    }
    _downstreamSubscribers.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected synchronized void doComplete()
  {
    for ( final Subscriber<? super T> subscriber : _downstreamSubscribers )
    {
      subscriber.onComplete();
    }
    _downstreamSubscribers.clear();
  }
}
