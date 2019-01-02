package streak.internal.transforming;

import java.util.HashSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Flow;
import streak.internal.AbstractStream;
import streak.schedulers.CircularBuffer;

final class MergeOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Flow.Stream<Flow.Stream<T>> _upstream;
  private final int _maxConcurrency;

  MergeOperator( @Nonnull final Flow.Stream<Flow.Stream<T>> upstream,
                 final int maxConcurrency )
  {
    _upstream = Objects.requireNonNull( upstream );
    _maxConcurrency = maxConcurrency;
    assert maxConcurrency > 0;
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber, _maxConcurrency ) );
  }

  private static final class WorkerSubscription<T>
    extends TransformSubscription<Flow.Stream<T>, T>
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

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> downstreamSubscriber,
                        final int maxConcurrency )
    {
      super( downstreamSubscriber );
      _maxConcurrency = maxConcurrency;
      _pendingUpstream = null;
    }

    /**
     * {@inheritDoc}
     */
    public void onNext( @Nonnull final Flow.Stream<T> item )
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

    private void completeInner( @Nonnull final InnerSubscription<T> innerSubscription )
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

  private static final class InnerSubscription<T>
    implements Flow.Subscription, Flow.Subscriber<T>
  {
    @Nonnull
    private final WorkerSubscription<T> _container;
    @Nonnull
    private final Flow.Stream<T> _upstream;
    @Nonnull
    private final Flow.Subscriber<? super T> _downstream;
    private Flow.Subscription _upstreamSubscription;

    InnerSubscription( @Nonnull final WorkerSubscription<T> container,
                       @Nonnull final Flow.Subscriber<? super T> downstream,
                       @Nonnull final Flow.Stream<T> upstream )
    {
      _container = Objects.requireNonNull( container );
      _downstream = Objects.requireNonNull( downstream );
      _upstream = Objects.requireNonNull( upstream );
    }

    void pushData()
    {
      _upstream.subscribe( this );
    }

    @Override
    public void dispose()
    {
      if ( null != _upstreamSubscription )
      {
        _upstreamSubscription.dispose();
        _upstreamSubscription = null;
      }
    }

    @Override
    public boolean isDisposed()
    {
      return null == _upstreamSubscription;
    }

    @Override
    public void onSubscribe( @Nonnull final Flow.Subscription subscription )
    {
      assert null == _upstreamSubscription;
      _upstreamSubscription = subscription;
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      _downstream.onNext( item );
    }

    @Override
    public void onError( @Nonnull final Throwable throwable )
    {
      _upstreamSubscription = null;
      _container.onError( throwable );
    }

    @Override
    public void onComplete()
    {
      _upstreamSubscription = null;
      _container.completeInner( this );
    }
  }
}
