package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class ThrottleOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final TimeoutForItemFn<T> _timeoutForItemFn;

  ThrottleOperator( @Nullable final String name,
                    @Nonnull final Stream<T> upstream,
                    @Nonnull final TimeoutForItemFn<T> timeoutForItemFn )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "throttle" ) : null, upstream );
    _timeoutForItemFn = timeoutForItemFn;
  }

  @Override
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractThrottlingSubscription<T, ThrottleOperator<T>>
  {
    WorkerSubscription( @Nonnull final ThrottleOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    final void doOnNext( final int now, @Nonnull final T item )
    {
      if ( !hasNextItem() )
      {
        scheduleTaskForItem( item, getStream()._timeoutForItemFn.getTimeout( item ) );
      }
    }
  }
}
