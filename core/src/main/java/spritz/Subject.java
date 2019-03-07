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
  private final Set<Subscriber<? super T>> _downstreamSubscribers = new HashSet<>();
  @Nullable
  private Throwable _error;
  private boolean _complete;

  Subject( @Nullable final String name )
  {
    super( name );
  }

  final void doSubscribe( @Nonnull Subscriber<? super T> subscriber )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> !_downstreamSubscribers.contains( subscriber ),
                    () -> "Spritz-0010: Invoked Subject.subscribe(...) when subscriber is already subscribed." );
    }
    completeSubscribe( subscriber );
  }

  void removeSubscriber( @Nonnull final Subscriber<? super T> subscriber )
  {
    _downstreamSubscribers.remove( subscriber );
  }

  void addSubscriber( @Nonnull final Subscriber<? super T> subscriber )
  {
    _downstreamSubscribers.add( subscriber );
  }

  boolean isSubscriber( @Nonnull final Subscriber<? super T> subscriber )
  {
    return _downstreamSubscribers.contains( subscriber );
  }

  void completeSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( () -> removeSubscriber( subscriber ) );
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
      addSubscriber( subscriber );
    }
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

  private void terminateUpstreamSubscribers()
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

  final boolean isComplete()
  {
    return _complete;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDone()
  {
    return _complete || null != _error;
  }

  void doNext( @Nonnull final T item )
  {
    for ( final Subscriber<? super T> subscriber : _downstreamSubscribers )
    {
      subscriber.onNext( item );
    }
  }

  final void doError( @Nonnull final Throwable error )
  {
    for ( final Subscriber<? super T> subscriber : _downstreamSubscribers )
    {
      subscriber.onError( error );
    }
    _downstreamSubscribers.clear();
  }

  final void doComplete()
  {
    for ( final Subscriber<? super T> subscriber : _downstreamSubscribers )
    {
      subscriber.onComplete();
    }
    _downstreamSubscribers.clear();
  }
}
