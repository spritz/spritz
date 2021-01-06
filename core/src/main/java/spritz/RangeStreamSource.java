package spritz;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A synchronous implementation of the {@link spritz.Stream} that can
 * be subscribed to multiple times and each individual subscription
 * will receive range of monotonically increasing integer values on demand.
 */
final class RangeStreamSource
  extends Stream<Integer>
{
  /**
   * The starting value of the range.
   */
  private final int _start;
  /**
   * The number of items to emit.
   */
  private final int _count;

  /**
   * Constructs a RangeStreamSource instance with the given start and count values
   * that yields a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   */
  RangeStreamSource( @Nullable final String name, final int start, final int count )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "range", start + ", " + count ) : null );
    assert count >= 0;
    _start = start;
    _count = count;
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull Subscriber<? super Integer> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription( this, subscriber );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
    return subscription;
  }

  private static final class WorkerSubscription
    extends AbstractStreamSubscription<Integer, RangeStreamSource>
  {
    WorkerSubscription( @Nonnull final RangeStreamSource stream, @Nonnull final Subscriber<? super Integer> subscriber )
    {
      super( stream, subscriber );
    }

    void pushData()
    {
      final RangeStreamSource stream = getStream();
      final int start = stream._start;
      final int end = start + stream._count - 1;
      int current = start;
      while ( current <= end && isNotDone() )
      {
        final int value = current;
        current++;
        getSubscriber().onItem( value );
      }
      if ( isNotDone() )
      {
        getSubscriber().onComplete();
      }
    }
  }
}
