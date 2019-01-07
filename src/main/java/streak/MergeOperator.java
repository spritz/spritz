package streak;

import java.util.HashSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.schedulers.CircularBuffer;

final class MergeOperator<T>
  implements Stream<T>
{
  @Nonnull
  private final Stream<Stream<T>> _upstream;
  private final int _maxConcurrency;

  MergeOperator( @Nonnull final Stream<Stream<T>> upstream,
                 final int maxConcurrency )
  {
    _upstream = Objects.requireNonNull( upstream );
    _maxConcurrency = maxConcurrency;
    assert maxConcurrency > 0;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber, _maxConcurrency ) );
  }

  private static final class WorkerSubscription<T>
    extends TransformSubscription<Stream<T>, T>
    implements InnerSubscription.ContainerSubscription<T>
  {
    /**
     * The maximum number of stream that have been subscribed to at one time.
     */
    private final int _maxConcurrency;
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
     * This will be [0,maxConcurrency] when the subscription is not disposed and -1 when the subscription is disposed.
     */
    private int _activeCount;
    /**
     * Flag indicating that the upstream has completed. If the upstream has completed and there
     * are no elements left in the buffer then the downstream is completed.
     */
    private boolean _upstreamCompleted;

    WorkerSubscription( @Nonnull final Subscriber<? super T> downstreamSubscriber,
                        final int maxConcurrency )
    {
      super( downstreamSubscriber );
      _maxConcurrency = maxConcurrency;
      _pendingUpstream = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext( @Nonnull final Stream<T> item )
    {
      final InnerSubscription<T> subscription = new InnerSubscription<>( this, getDownstreamSubscriber(), item );
      if ( _activeCount < _maxConcurrency )
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
    public void onError( @Nonnull final Throwable throwable )
    {
      _activeCount = -1;
      if ( null != _pendingUpstream )
      {
        _pendingUpstream.clear();
      }
      _activeStreams.forEach( activeStream -> activeStream.dispose() );
      _activeStreams.clear();
      getDownstreamSubscriber().onError( throwable );
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
      getDownstreamSubscriber().onComplete();
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
