package spritz;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

public class Subject<T>
  extends Stream<T>
  implements EventEmitter<T>
{
  private final Set<ForwardToSubjectSubscriber<T>> _upstreamSubscribers = new HashSet<>();
  private final Set<DownstreamSubscription> _downstreamSubscriptions = new HashSet<>();
  @Nullable
  private Throwable _error;
  private boolean _complete;

  Subject( @Nullable final String name )
  {
    super( Spritz.areNamesEnabled() ? Stream.generateName( name, "subject" ) : null );
  }

  final void doSubscribe( @Nonnull Subscriber<? super T> subscriber )
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

  /**
   * {@inheritDoc}
   */
  @Override
  public final void next( @Nonnull final T item )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> null == _error,
                    () -> "Spritz-0023: Subject.next(...) invoked after Subject.error(...) invoked." );
      apiInvariant( () -> !_complete,
                    () -> "Spritz-0024: Subject.next(...) invoked after Subject.complete() invoked." );
    }
    doNext( item );
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
                    () -> "Spritz-0025: Subject.error(...) invoked after Subject.error(...) invoked." );
      apiInvariant( () -> !_complete,
                    () -> "Spritz-0026: Subject.error(...) invoked after Subject.complete() invoked." );
    }
    _error = error;
    doError( error );
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
                 () -> "Spritz-0027: Subject.complete(...) invoked after Subject.error(...) invoked." );
      invariant( () -> !_complete,
                 () -> "Spritz-0028: Subject.complete(...) invoked after Subject.complete() invoked." );
    }
    _complete = true;
    doComplete();
    terminateUpstreamSubscribers();
  }

  final void terminateUpstreamSubscribers()
  {
    for ( final ForwardToSubjectSubscriber<T> subscriber : new ArrayList<>( _upstreamSubscribers ) )
    {
      subscriber.cancel();
    }
    _upstreamSubscribers.clear();
  }

  @Nonnull
  final ForwardToSubjectSubscriber<T> newUpstreamSubscriber()
  {
    final ForwardToSubjectSubscriber<T> subscriber = new ForwardToSubjectSubscriber<>( this );
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
  public boolean isDone()
  {
    return _complete || null != _error;
  }

  final boolean hasUpstreamSubscribers()
  {
    return !_upstreamSubscribers.isEmpty();
  }

  void doNext( @Nonnull final T item )
  {
    for ( final DownstreamSubscription subscription : _downstreamSubscriptions )
    {
      subscription.getSubscriber().onNext( item );
    }
  }

  final void doError( @Nonnull final Throwable error )
  {
    for ( final DownstreamSubscription subscription : _downstreamSubscriptions )
    {
      subscription.getSubscriber().onError( error );
    }
    _downstreamSubscriptions.clear();
  }

  final void doComplete()
  {
    for ( final DownstreamSubscription subscription : _downstreamSubscriptions )
    {
      subscription.getSubscriber().onComplete();
    }
    _downstreamSubscriptions.clear();
  }

  final class DownstreamSubscription
    extends AbstractSubscription<T>
    implements Subscription
  {
    DownstreamSubscription( @Nonnull final Subscriber<? super T> subscriber )
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
      return Subject.this.getQualifiedName();
    }
  }
}
