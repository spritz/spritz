package spritz;

import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class MergeOperator<T>
  extends AbstractStream<Stream<T>, T>
{
  /**
   * The maximum number of streams that can be subscribed to at one time.
   */
  private final int _maxConcurrency;

  MergeOperator( @Nullable final String name, @Nonnull final Stream<Stream<T>> upstream, final int maxConcurrency )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "merge", String.valueOf( maxConcurrency ) ) : null,
           upstream );
    _maxConcurrency = maxConcurrency;
    assert maxConcurrency > 0;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<Stream<T>, T, MergeOperator<T>>
    implements InnerSubscription.ContainerSubscription<T>
  {
    /**
     * The streams that have been received from upstream but have yet to be subscribed.
     */
    @Nullable
    private CircularBuffer<InnerSubscription<T>> _pendingUpstream;
    /**
     * The streams that have been received from upstream but have yet to be subscribed.
     */
    @Nonnull
    private final HashSet<InnerSubscription<T>> _activeStreams = new HashSet<>();
    /**
     * The number of buffers that are currently subscribed to.
     * This will be [0,maxConcurrency] when the subscription is not cancelled and -1 when the subscription is cancelled.
     */
    private int _activeCount;
    /**
     * Flag indicating that the upstream has completed. If the upstream has completed and there
     * are no items left in the buffer then the downstream is completed.
     */
    private boolean _upstreamCompleted;

    WorkerSubscription( @Nonnull final MergeOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
      _pendingUpstream = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext( @Nonnull final Stream<T> item )
    {
      final InnerSubscription<T> subscription = new InnerSubscription<>( item, getSubscriber(), this );
      if ( _activeCount < getStream()._maxConcurrency )
      {
        _activeCount++;
        _activeStreams.add( subscription );
        subscription.pushData();
      }
      else
      {
        if ( null == _pendingUpstream )
        {
          _pendingUpstream = new CircularBuffer<>( 10 );
        }
        _pendingUpstream.add( subscription );
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError( @Nonnull final Throwable error )
    {
      _activeCount = -1;
      if ( null != _pendingUpstream )
      {
        _pendingUpstream.clear();
      }
      _activeStreams.forEach( InnerSubscription::cancel );
      _activeStreams.clear();
      getSubscriber().onError( error );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete()
    {
      _upstreamCompleted = true;
      if ( 0 == _activeCount )
      {
        doComplete();
      }
    }

    private void doComplete()
    {
      _activeCount = -1;
      getSubscriber().onComplete();
    }

    @Override
    public void completeInner( @Nonnull final InnerSubscription<T> innerSubscription )
    {
      final boolean found = _activeStreams.remove( innerSubscription );
      assert found;
      _activeCount--;
      if ( _upstreamCompleted && 0 == _activeCount && ( null == _pendingUpstream || _pendingUpstream.isEmpty() ) )
      {
        doComplete();
      }
      else
      {
        if ( null != _pendingUpstream )
        {
          final InnerSubscription<T> nextInnerSubscription = _pendingUpstream.pop();
          if ( null != nextInnerSubscription )
          {
            _activeCount++;
            nextInnerSubscription.pushData();
            _activeStreams.add( nextInnerSubscription );
          }
        }
      }
    }
  }
}
