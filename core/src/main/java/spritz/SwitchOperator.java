package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class SwitchOperator<T>
  extends AbstractStream<Stream<T>, T>
{
  SwitchOperator( @Nonnull final Stream<Stream<T>> upstream )
  {
    super( upstream );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<Stream<T>, T, SwitchOperator<T>>
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

    WorkerSubscription( @Nonnull final SwitchOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
      _activeStream = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext( @Nonnull final Stream<T> item )
    {
      if ( null != _activeStream )
      {
        _activeStream.cancel();
      }
      _activeStream = new InnerSubscription<>( this, getSubscriber(), item );
      _activeStream.pushData();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError( @Nonnull final Throwable error )
    {
      _activeStream = null;
      getSubscriber().onError( error );
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
        getSubscriber().onComplete();
      }
    }

    @Override
    public void completeInner( @Nonnull final InnerSubscription<T> innerSubscription )
    {
      assert _activeStream == innerSubscription;
      _activeStream = null;
      if ( _upstreamCompleted )
      {
        getSubscriber().onComplete();
      }
    }
  }
}
