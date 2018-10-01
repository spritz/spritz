package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A synchronous implementation of the {@link Flow.Publisher} that can
 * be subscribed to multiple times and each individual subscription
 * will receive range of monotonically increasing integer values on demand.
 */
final class RangePublisher
  extends AbstractPublisher<Integer>
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
   * Constructs a RangePublisher instance with the given start and count values
   * that yields a sequence of [start, start + count).
   *
   * @param start the starting value of the range
   * @param count the number of items to emit
   */
  RangePublisher( final int start, final int count )
  {
    _start = start;
    _count = count;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void subscribe( @Nonnull Flow.Subscriber<? super Integer> subscriber )
  {
    Objects.requireNonNull( subscriber ).onSubscribe( new WorkerSubscription( subscriber, _start, _start + _count ) );
  }

  private static final class WorkerSubscription
    implements Flow.Subscription
  {
    private final Flow.Subscriber<? super Integer> _subscriber;
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
    WorkerSubscription( @Nonnull final Flow.Subscriber<? super Integer> subscriber, int start, int end )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _current = start;
      _end = end;
    }

    @Override
    public void request( final int count )
    {
      assert count > 0;
      if ( isNotDisposed() )
      {
        final int maxSize = _end;
        final int requestEnd = Math.min( _current + count, maxSize );
        do
        {
          final int current = _current;
          _current++;
          _subscriber.onNext( current );
          // Subscriber can call cancel in onNext so we have to test against _current rather than using local index
          // Should have generic test to verify this.
        }
        while ( _current < requestEnd );

        if ( _current == maxSize )
        {
          _subscriber.onComplete();
          dispose();
        }
      }
    }

    @Override
    public boolean isDisposed()
    {
      return _current > _end;
    }

    @Override
    public void dispose()
    {
      _current = _end + 1;
    }
  }
}
