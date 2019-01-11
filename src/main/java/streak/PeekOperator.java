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
  private final Consumer<Subscription> _onSubscription;
  @Nullable
  private final Consumer<Subscription> _afterSubscription;
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
  private final Runnable _onCancel;
  @Nullable
  private final Runnable _afterCancel;

  PeekOperator( @Nonnull final Stream<? extends T> upstream,
                @Nullable final Consumer<Subscription> onSubscription,
                @Nullable final Consumer<Subscription> afterSubscription,
                @Nullable final Consumer<? super T> onNext,
                @Nullable final Consumer<? super T> afterNext,
                @Nullable final Consumer<Throwable> onError,
                @Nullable final Consumer<Throwable> afterError,
                @Nullable final Runnable onComplete,
                @Nullable final Runnable afterComplete,
                @Nullable final Runnable onCancel,
                @Nullable final Runnable afterCancel )
  {
    _upstream = Objects.requireNonNull( upstream );
    _onSubscription = onSubscription;
    _afterSubscription = afterSubscription;
    _onNext = onNext;
    _afterNext = afterNext;
    _onError = onError;
    _afterError = afterError;
    _onComplete = onComplete;
    _afterComplete = afterComplete;
    _onCancel = onCancel;
    _afterCancel = afterCancel;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber,
                                                   _onSubscription,
                                                   _afterSubscription,
                                                   _onNext,
                                                   _afterNext,
                                                   _onError,
                                                   _afterError,
                                                   _onComplete,
                                                   _afterComplete,
                                                   _onCancel,
                                                   _afterCancel ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractSubscription
    implements Subscriber<T>
  {
    @Nonnull
    private final Subscriber<? super T> _downstreamSubscriber;
    @Nullable
    private final Consumer<Subscription> _onSubscription;
    @Nullable
    private final Consumer<Subscription> _afterSubscription;
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
    private final Runnable _onCancel;
    @Nullable
    private final Runnable _afterCancel;
    private boolean _done;

    WorkerSubscription( @Nonnull final Subscriber<? super T> downstreamSubscriber,
                        @Nullable final Consumer<Subscription> onSubscription,
                        @Nullable final Consumer<Subscription> afterSubscription,
                        @Nullable final Consumer<? super T> onNext,
                        @Nullable final Consumer<? super T> afterNext,
                        @Nullable final Consumer<Throwable> onError,
                        @Nullable final Consumer<Throwable> afterError,
                        @Nullable final Runnable onComplete,
                        @Nullable final Runnable afterComplete,
                        @Nullable final Runnable onCancel,
                        @Nullable final Runnable afterCancel )
    {
      _downstreamSubscriber = Objects.requireNonNull( downstreamSubscriber );
      _onSubscription = onSubscription;
      _afterSubscription = afterSubscription;
      _onNext = onNext;
      _afterNext = afterNext;
      _onError = onError;
      _afterError = afterError;
      _onComplete = onComplete;
      _afterComplete = afterComplete;
      _onCancel = onCancel;
      _afterCancel = afterCancel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      setUpstream( subscription );
      if ( null != _onSubscription )
      {
        _onSubscription.accept( subscription );
      }
      _downstreamSubscriber.onSubscribe( this );
      if ( null != _afterSubscription )
      {
        _afterSubscription.accept( subscription );
      }
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
      _done = true;
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
      _done = true;
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
    public void cancel()
    {
      if ( !_done )
      {
        _done = true;
        if ( null != _onCancel )
        {
          _onCancel.run();
        }
        getUpstream().cancel();
        if ( null != _afterCancel )
        {
          _afterCancel.run();
        }
      }
    }
  }
}
