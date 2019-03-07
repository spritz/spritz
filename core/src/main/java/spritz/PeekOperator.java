package spritz;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class PeekOperator<T>
  extends AbstractStream<T, T>
{
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

  PeekOperator( @Nullable final String name,
                @Nonnull final Stream<T> upstream,
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
    super( Spritz.areNamesEnabled() ? generateName( name, "peek" ) : null, upstream );
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
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends PassThroughSubscription<T, PeekOperator<T>>
    implements Subscriber<T>
  {
    WorkerSubscription( @Nonnull final PeekOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      setUpstream( subscription );
      final Consumer<Subscription> onSubscription = getStream()._onSubscription;
      if ( null != onSubscription )
      {
        onSubscription.accept( subscription );
      }
      getSubscriber().onSubscribe( this );
      final Consumer<Subscription> afterSubscription = getStream()._afterSubscription;
      if ( null != afterSubscription )
      {
        afterSubscription.accept( subscription );
      }
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      final Consumer<? super T> onNext = getStream()._onNext;
      if ( null != onNext )
      {
        onNext.accept( item );
      }
      super.onNext( item );
      final Consumer<? super T> afterNext = getStream()._afterNext;
      if ( null != afterNext )
      {
        afterNext.accept( item );
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError( @Nonnull final Throwable error )
    {
      markAsCancelled();
      final Consumer<Throwable> onError = getStream()._onError;
      if ( null != onError )
      {
        onError.accept( error );
      }
      super.onError( error );
      final Consumer<Throwable> afterError = getStream()._afterError;
      if ( null != afterError )
      {
        afterError.accept( error );
      }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComplete()
    {
      markAsCancelled();
      final Runnable onComplete = getStream()._onComplete;
      if ( null != onComplete )
      {
        onComplete.run();
      }
      super.onComplete();
      final Runnable afterComplete = getStream()._afterComplete;
      if ( null != afterComplete )
      {
        afterComplete.run();
      }
    }

    @Override
    final void doCancel()
    {
      final Runnable onCancel = getStream()._onCancel;
      if ( null != onCancel )
      {
        onCancel.run();
      }
      super.doCancel();
      final Runnable afterCancel = getStream()._afterCancel;
      if ( null != afterCancel )
      {
        afterCancel.run();
      }
    }
  }
}
