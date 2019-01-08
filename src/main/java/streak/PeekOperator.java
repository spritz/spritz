package streak;

import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class PeekOperator<T>
  implements Stream<T>
{
  @Nonnull
  private final Stream<? extends T> _upstream;
  @Nullable
  private final Consumer<? super T> _onNext;
  @Nullable
  private final Consumer<? super T> _afterNext;
  @Nullable
  private final Consumer<Throwable> _onError;
  @Nullable
  private final Consumer<Throwable> _afterError;
  @Nullable
  private final Runnable _onComplete;
  @Nullable
  private final Runnable _afterComplete;
  @Nullable
  private final Runnable _onDispose;
  @Nullable
  private final Runnable _afterDispose;

  PeekOperator( @Nonnull final Stream<? extends T> upstream,
                @Nullable final Consumer<? super T> onNext,
                @Nullable final Consumer<? super T> afterNext,
                @Nullable final Consumer<Throwable> onError,
                @Nullable final Consumer<Throwable> afterError,
                @Nullable final Runnable onComplete,
                @Nullable final Runnable afterComplete,
                @Nullable final Runnable onDispose,
                @Nullable final Runnable afterDispose )
  {
    _upstream = Objects.requireNonNull( upstream );
    _onNext = onNext;
    _afterNext = afterNext;
    _onError = onError;
    _afterError = afterError;
    _onComplete = onComplete;
    _afterComplete = afterComplete;
    _onDispose = onDispose;
    _afterDispose = afterDispose;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber,
                                                   _onNext,
                                                   _afterNext,
                                                   _onError,
                                                   _afterError,
                                                   _onComplete,
                                                   _afterComplete,
                                                   _onDispose,
                                                   _afterDispose ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractSubscription
    implements Subscriber<T>
  {
    @Nonnull
    private final Subscriber<? super T> _downstreamSubscriber;
    @Nullable
    private final Consumer<? super T> _onNext;
    @Nullable
    private final Consumer<? super T> _afterNext;
    @Nullable
    private final Consumer<Throwable> _onError;
    @Nullable
    private final Consumer<Throwable> _afterError;
    @Nullable
    private final Runnable _onComplete;
    @Nullable
    private final Runnable _afterComplete;
    @Nullable
    private final Runnable _onDispose;
    @Nullable
    private final Runnable _afterDispose;

    WorkerSubscription( @Nonnull final Subscriber<? super T> downstreamSubscriber,
                        @Nullable final Consumer<? super T> onNext,
                        @Nullable final Consumer<? super T> afterNext,
                        @Nullable final Consumer<Throwable> onError,
                        @Nullable final Consumer<Throwable> afterError,
                        @Nullable final Runnable onComplete,
                        @Nullable final Runnable afterComplete,
                        @Nullable final Runnable onDispose,
                        @Nullable final Runnable afterDispose )
    {
      _downstreamSubscriber = Objects.requireNonNull( downstreamSubscriber );
      _onNext = onNext;
      _afterNext = afterNext;
      _onError = onError;
      _afterError = afterError;
      _onComplete = onComplete;
      _afterComplete = afterComplete;
      _onDispose = onDispose;
      _afterDispose = afterDispose;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
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
      if ( null != _afterNext )
      {
        _afterNext.accept( item );
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError( @Nonnull final Throwable throwable )
    {
      if ( null != _onError )
      {
        _onError.accept( throwable );
      }
      _downstreamSubscriber.onError( throwable );
      if ( null != _afterError )
      {
        _afterError.accept( throwable );
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete()
    {
      if ( null != _onComplete )
      {
        _onComplete.run();
      }
      _downstreamSubscriber.onComplete();
      if ( null != _afterComplete )
      {
        _afterComplete.run();
      }
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
        if ( null != _afterDispose )
        {
          _afterDispose.run();
        }
      }
    }
  }
}
