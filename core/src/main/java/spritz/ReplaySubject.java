package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A ReplaySubject records multiple values from the stream execution and replays them to new subscribers.
 */
final class ReplaySubject<T>
  extends Subject<T>
{
  private static final int INITIAL_CAPACITY = 10;
  static final int DEFAULT_VALUE = -1;
  @Nonnull
  private final CircularBuffer<Entry<T>> _buffer;
  private final int _maxSize;
  private final int _maxAge;

  ReplaySubject( @Nullable final String name, final int maxSize, final int maxAge )
  {
    super( Spritz.areNamesEnabled() ?
           generateName( name,
                         "replaySubject",
                         ( DEFAULT_VALUE == maxSize ? "unbound" : String.valueOf( maxSize ) ) + "," +
                         ( DEFAULT_VALUE == maxAge ? "unbound" : String.valueOf( maxAge ) ) ) :
           null );
    assert maxSize > 0 || DEFAULT_VALUE == maxSize;
    assert maxAge > 0 || DEFAULT_VALUE == maxAge;
    _buffer = new CircularBuffer<>( Math.min( Math.max( 1, maxSize ), INITIAL_CAPACITY ) );
    _maxSize = maxSize;
    _maxAge = maxAge;
  }

  @Override
  void completeSubscribe( @Nonnull final DownstreamSubscription subscription )
  {
    final int now = Scheduler.now();
    final Subscriber<? super T> subscriber = subscription.getSubscriber();
    final int size = _buffer.size();
    for ( int i = 0; i < size; i++ )
    {
      final Entry<T> entry = _buffer.get( i );
      assert null != entry;
      if ( DEFAULT_VALUE == _maxAge || entry.getTime() + _maxAge >= now )
      {
        if ( isNotDone() && subscription.isNotDone() )
        {
          subscriber.onNext( entry.getItem() );
        }
        else
        {
          return;
        }
      }
    }
  }

  @Override
  void downstreamNext( @Nonnull final T item )
  {
    final int size = _buffer.size();
    if ( DEFAULT_VALUE != _maxSize && size == _maxSize )
    {
      _buffer.pop();
    }
    if ( DEFAULT_VALUE != _maxAge && size == _buffer.getCapacity() )
    {
      final Entry<T> peek = _buffer.peek();
      assert null != peek;
      if ( peek._time + _maxAge < Scheduler.now() )
      {
        _buffer.pop();
      }
    }
    _buffer.add( new Entry<>( Scheduler.now(), item ) );
    super.downstreamNext( item );
  }

  private static final class Entry<T>
  {
    private final int _time;
    @Nonnull
    private final T _item;

    Entry( final int time, @Nonnull final T item )
    {
      _time = time;
      _item = Objects.requireNonNull( item );
    }

    int getTime()
    {
      return _time;
    }

    @Nonnull
    T getItem()
    {
      return _item;
    }
  }
}
