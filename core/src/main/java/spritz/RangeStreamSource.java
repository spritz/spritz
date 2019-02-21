package spritz;

import java.util.Objects;
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

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doSubscribe( @Nonnull Subscriber<? super Integer> subscriber )
  {
    final WorkerSubscription subscription = new WorkerSubscription( subscriber, _start, _start + _count - 1 );
    subscriber.onSubscribe( subscription );
    subscription.pushData();
  }

  private static final class WorkerSubscription
    implements Subscription
  {
    private final Subscriber<? super Integer> _subscriber;
    /**
     * The end index (exclusive).
     */
    private final int _end;
    /**
     * The current value and within the [_start, _start + count) range that will be emitted as subscriber.onNext().
     */
    private int _current;

    /**
     * Constructs a stateful WorkerSubscription that emits signals to the given
     * downstream from an integer range of [_start, end).
     *
     * @param subscriber the Subscriber receiving the integer values and the completion signal.
     * @param start      the first integer value emitted, _start of the range
     * @param end        the end of the range, exclusive
     */
    WorkerSubscription( @Nonnull final Subscriber<? super Integer> subscriber, int start, int end )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _end = end;
      _current = start;
    }

    void pushData()
    {
      while ( _current <= _end && isNotCancelled() )
      {
        final int value = _current;
        _current++;
        _subscriber.onNext( value );
      }
      if ( isNotCancelled() )
      {
        _subscriber.onComplete();
        cancel();
      }
    }

    private boolean isNotCancelled()
    {
      return -1 != _current;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
      _current = -1;
    }
  }
}
