package streak.internal.transforming;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Stream;
import streak.Subscriber;
import streak.internal.AbstractStream;

final class ExhaustOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Stream<Stream<T>> _upstream;

  ExhaustOperator( @Nonnull final Stream<Stream<T>> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends TransformSubscription<Stream<T>, T>
    implements InnerSubscription.ContainerSubscription<T>
  {
    /**
     * The streams that have been received from upstream but have yet to be subscribed.
     */
    @Nullable
    private InnerSubscription<T> _activeStream;
    /**
     * Flag indicating that the upstream has completed. If the upstream has completed and the
     * activeStream is null then the downstream is completed.
     */
    private boolean _upstreamCompleted;

    WorkerSubscription( @Nonnull final Subscriber<? super T> downstreamSubscriber )
    {
      super( downstreamSubscriber );
      _activeStream = null;
    }

    /**
     * {@inheritDoc}
     */
    public void onNext( @Nonnull final Stream<T> item )
    {
      if ( null == _activeStream )
      {
        _activeStream = new InnerSubscription<>( this, getDownstreamSubscriber(), item );
        _activeStream.pushData();
      }
    }

    /**
     * {@inheritDoc}
     */
    public void onError( @Nonnull final Throwable throwable )
    {
      _activeStream = null;
      getDownstreamSubscriber().onError( throwable );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete()
    {
      _upstreamCompleted = true;
      if ( null == _activeStream )
      {
        getDownstreamSubscriber().onComplete();
      }
    }

    @Override
    public void completeInner( @Nonnull final InnerSubscription<T> innerSubscription )
    {
      assert _activeStream == innerSubscription;
      _activeStream = null;
      if ( _upstreamCompleted )
      {
        getDownstreamSubscriber().onComplete();
      }
    }
  }
}
