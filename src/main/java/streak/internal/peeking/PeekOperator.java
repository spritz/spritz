package streak.internal.peeking;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import streak.Flow;
import streak.internal.AbstractChainedSubscription;
import streak.internal.AbstractStream;

final class PeekOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Flow.Stream<? extends T> _upstream;
  @Nullable
  private final Consumer<? super T> _onNext;
  @Nullable
  private final Consumer<Throwable> _onError;
  @Nullable
  private final Runnable _onComplete;
  @Nullable
  private final Runnable _onDispose;

  PeekOperator( @Nonnull final Flow.Stream<? extends T> upstream,
                @Nullable final Consumer<? super T> onNext,
                @Nullable final Consumer<Throwable> onError,
                @Nullable final Runnable onComplete,
                @Nullable final Runnable onDispose )
  {
    _upstream = Objects.requireNonNull( upstream );
    _onNext = onNext;
    _onError = onError;
    _onComplete = onComplete;
    _onDispose = onDispose;
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber, _onNext, _onError, _onComplete, _onDispose ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractChainedSubscription
    implements Flow.Subscriber<T>
  {
    @Nonnull
    private final Flow.Subscriber<? super T> _downstreamSubscriber;
    @Nullable
    private final Consumer<? super T> _onNext;
    @Nullable
    private final Consumer<Throwable> _onError;
    @Nullable
    private final Runnable _onComplete;
    @Nullable
    private final Runnable _onDispose;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> downstreamSubscriber,
                        @Nullable final Consumer<? super T> onNext,
                        @Nullable final Consumer<Throwable> onError,
                        @Nullable final Runnable onComplete,
                        @Nullable final Runnable onDispose )
    {
      _downstreamSubscriber = Objects.requireNonNull( downstreamSubscriber );
      _onNext = onNext;
      _onError = onError;
      _onComplete = onComplete;
      _onDispose = onDispose;
    }

    /**
     * {@inheritDoc}
     */
    public void onSubscribe( @Nonnull final Flow.Subscription subscription )
    {
      setUpstream( subscription );
      _downstreamSubscriber.onSubscribe( this );
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      if ( null != _onNext )
      {
        _onNext.accept( item );
      }
      _downstreamSubscriber.onNext( item );
    }

    /**
     * {@inheritDoc}
     */
    public void onError( @Nonnull final Throwable throwable )
    {
      if ( null != _onError )
      {
        _onError.accept( throwable );
      }
      _downstreamSubscriber.onError( throwable );
    }

    /**
     * {@inheritDoc}
     */
    public void onComplete()
    {
      if ( null != _onComplete )
      {
        _onComplete.run();
      }
      _downstreamSubscriber.onComplete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDisposed()
    {
      return getUpstream().isDisposed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose()
    {
      if ( isNotDisposed() )
      {
        if ( null != _onDispose )
        {
          _onDispose.run();
        }
        getUpstream().dispose();
      }
    }
  }
}
