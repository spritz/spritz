package spritz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

public abstract class Hub<MessageInT, MessageOutT>
  extends Stream<MessageOutT>
  implements EventEmitter<MessageInT>
{
  private final Set<ForwardToHubSubscriber<MessageOutT>> _upstreamSubscribers = new HashSet<>();
  private final Set<DownstreamSubscription> _downstreamSubscriptions = new HashSet<>();
  @Nullable
  private Throwable _error;
  private boolean _complete;

  Hub( @Nullable final String name )
  {
    super( Spritz.areNamesEnabled() ? Stream.generateName( name, "subject" ) : null );
  }

  final void doSubscribe( @Nonnull Subscriber<? super MessageOutT> subscriber )
  {
    final DownstreamSubscription subscription = new DownstreamSubscription( subscriber );
    subscriber.onSubscribe( subscription );
    if ( subscription.isNotDone() )
    {
      completeSubscribe( subscription );
    }
    if ( subscription.isNotDone() )
    {
      if ( _complete )
      {
        subscriber.onComplete();
      }
      else if ( null != _error )
      {
        subscriber.onError( _error );
      }
      else
      {
        _downstreamSubscriptions.add( subscription );
      }
    }
  }

  void completeSubscribe( @Nonnull final DownstreamSubscription subscription )
  {
  }

  final void ensureNextValid()
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> null == _error,
                    () -> "Hub-0023: Hub.next(...) invoked after Hub.error(...) invoked." );
      apiInvariant( () -> !_complete,
                    () -> "Hub-0024: Hub.next(...) invoked after Hub.complete() invoked." );
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void error( @Nonnull final Throwable error )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> null == _error,
                    () -> "Spritz-0025: Hub.error(...) invoked after Hub.error(...) invoked." );
      apiInvariant( () -> !_complete,
                    () -> "Spritz-0026: Hub.error(...) invoked after Hub.complete() invoked." );
    }
    _error = error;
    Scheduler.current( () -> downstreamError( error ) );
    terminateUpstreamSubscribers();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void complete()
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      invariant( () -> null == _error,
                 () -> "Spritz-0027: Hub.complete(...) invoked after Hub.error(...) invoked." );
      invariant( () -> !_complete,
                 () -> "Spritz-0028: Hub.complete(...) invoked after Hub.complete() invoked." );
    }
    _complete = true;
    Scheduler.current( this::downstreamComplete );
    terminateUpstreamSubscribers();
  }

  final void terminateUpstreamSubscribers()
  {
    for ( final ForwardToHubSubscriber<MessageOutT> subscriber : new ArrayList<>( _upstreamSubscribers ) )
    {
      subscriber.cancel();
    }
    _upstreamSubscribers.clear();
  }

  @Nonnull
  final ForwardToHubSubscriber<MessageOutT> newUpstreamSubscriber()
  {
    final ForwardToHubSubscriber<MessageOutT> subscriber = new ForwardToHubSubscriber<>( this );
    _upstreamSubscribers.add( subscriber );
    return subscriber;
  }

  @Nullable
  final Throwable getError()
  {
    return _error;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isDone()
  {
    return _complete || null != _error;
  }

  final boolean hasUpstreamSubscribers()
  {
    return !_upstreamSubscribers.isEmpty();
  }

  void downstreamNext( @Nonnull final MessageOutT item )
  {
    for ( final DownstreamSubscription subscription : _downstreamSubscriptions )
    {
      subscription.getSubscriber().onNext( item );
    }
  }

  void downstreamError( @Nonnull final Throwable error )
  {
    for ( final DownstreamSubscription subscription : _downstreamSubscriptions )
    {
      subscription.getSubscriber().onError( error );
    }
    _downstreamSubscriptions.clear();
  }

  void downstreamComplete()
  {
    for ( final DownstreamSubscription subscription : _downstreamSubscriptions )
    {
      subscription.getSubscriber().onComplete();
    }
    _downstreamSubscriptions.clear();
  }

  final class DownstreamSubscription
    extends AbstractSubscription<MessageOutT>
    implements Subscription
  {
    DownstreamSubscription( @Nonnull final Subscriber<? super MessageOutT> subscriber )
    {
      super( subscriber );
    }

    @Override
    void doCancel()
    {
      _downstreamSubscriptions.remove( this );
    }

    @Override
    String getQualifiedName()
    {
      return Hub.this.getQualifiedName();
    }
  }
}
