package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class DebounceOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final TimeoutForItemFn<T> _timeoutForItemFn;

  DebounceOperator( @Nullable final String name,
                    @Nonnull final Stream<T> upstream,
                    @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "debounce" ) : null, upstream );
    _timeoutForItemFn = timeoutForItemFn;
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    getUpstream().subscribe( subscription );
    return subscription;
  }

  private static final class WorkerSubscription<T>
    extends AbstractThrottlingSubscription<T, DebounceOperator<T>>
  {
    WorkerSubscription( @Nonnull final DebounceOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    void doOnNext( final int now, @Nonnull final T item )
    {
      cancelPendingTask();
      scheduleTaskForItem( item, getStream()._timeoutForItemFn.getTimeout( item ) );
    }
  }
}
