package spritz;

import javax.annotation.Nonnull;

final class DebounceOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final TimeoutForItemFn<T> _timeoutForItemFn;

  DebounceOperator( @Nonnull final Publisher<T> upstream, @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    super( upstream );
    _timeoutForItemFn = timeoutForItemFn;
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _timeoutForItemFn ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractThrottlingSubscription<T>
  {
    @Nonnull
    private final TimeoutForItemFn<T> _timeoutForItemFn;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
    {
      super( subscriber );
      _timeoutForItemFn = timeoutForItemFn;
    }

    @Override
    protected void doOnNext( final int now, @Nonnull final T item )
    {
      cancelPendingTask();
      scheduleTaskForItem( item, _timeoutForItemFn.getTimeout( item ) );
    }
  }
}
