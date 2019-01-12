package streak;

import javax.annotation.Nonnull;

final class ThrottleWithTimeoutOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final TimeoutForItemFn<T> _timeoutForItemFn;

  ThrottleWithTimeoutOperator( @Nonnull final Stream<? extends T> upstream,
                               @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    super( upstream );
    _timeoutForItemFn = timeoutForItemFn;
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
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
    public void onNext( @Nonnull final T item )
    {
      clearPendingTask();
      scheduleTaskForItem( item, _timeoutForItemFn.getTimeout( item ) );
    }
  }
}
