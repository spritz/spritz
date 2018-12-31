package streak;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.internal.AbstractPublisher;

final class ConcatPublisher<T>
  extends AbstractPublisher<T>
{
  @Nonnull
  private final Flow.Stream<? extends T>[] _upstreams;

  ConcatPublisher( @Nonnull final Flow.Stream<? extends T>[] upstreams )
  {
    _upstreams = Objects.requireNonNull( upstreams );
    assert _upstreams.length > 0;
    for ( final Flow.Stream<? extends T> upstream : upstreams )
    {
      Objects.requireNonNull( upstream );
    }
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( subscriber, _upstreams );
    Objects.requireNonNull( subscriber ).onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription<T>
    implements Flow.Subscription, Flow.Subscriber<T>
  {
    @Nonnull
    private final Flow.Stream<? extends T>[] _upstreams;
    private final Flow.Subscriber<? super T> _downstream;
    private int _upstreamIndex = 0;
    private Flow.Subscription _upstreamSubscription;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> downstream,
                        @Nonnull final Flow.Stream<? extends T>[] upstreams )
    {
      assert upstreams.length > 0;
      _downstream = downstream;
      _upstreams = upstreams;
    }

    void pushData()
    {
      _upstreams[ 0 ].subscribe( this );
    }

    @Override
    public void dispose()
    {
      if ( null != _upstreamSubscription )
      {
        _upstreamIndex = -1;
        _upstreamSubscription.dispose();
        _upstreamSubscription = null;
      }
    }

    @Override
    public boolean isDisposed()
    {
      return _upstreamIndex < 0;
    }

    @Override
    public void onSubscribe( @Nonnull final Flow.Subscription subscription )
    {
      assert isNotDisposed();
      assert null == _upstreamSubscription;
      _upstreamSubscription = subscription;
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      assert isNotDisposed();
      _downstream.onNext( item );
    }

    @Override
    public void onError( @Nonnull final Throwable throwable )
    {
      assert isNotDisposed();
      _upstreamSubscription = null;
      _upstreamIndex = -1;
      _downstream.onError( throwable );
    }

    @Override
    public void onComplete()
    {
      assert isNotDisposed();
      _upstreamSubscription = null;
      if ( _upstreamIndex + 1 == _upstreams.length )
      {
        _downstream.onComplete();
        _upstreamIndex = -1;
      }
      else
      {
        _upstreamIndex++;
        _upstreams[ _upstreamIndex ].subscribe( this );
      }
    }
  }
}
