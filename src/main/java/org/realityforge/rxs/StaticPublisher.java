package org.realityforge.rxs;

import java.util.Objects;
import javax.annotation.Nonnull;

final class StaticPublisher<T>
  extends AbstractPublisher<T>
{
  private final T[] _data;

  StaticPublisher( @Nonnull final T[] data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    Objects.requireNonNull( subscriber ).onSubscribe( new StaticSubscription<T>( subscriber, _data ) );
  }

  private static final class StaticSubscription<T>
    implements Flow.Subscription
  {
    private final Flow.Subscriber<? super T> _subscriber;
    private final T[] _data;

    /**
     * Index into data.
     * _offset == _data.length implies next action is onComplete.
     * _offset == _data.length + 1 implies cancelled or onComplete has been invoked.
     */
    private int _offset;

    StaticSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber, @Nonnull final T[] data )
    {
      _subscriber = Objects.requireNonNull( subscriber );
      _data = Objects.requireNonNull( data );
    }

    @Override
    public void request( final int count )
    {
      assert count > 0;
      if ( !isDone() )
      {
        final int maxSize = _data.length;
        final int requestEnd = Math.min( _offset + count, maxSize );
        do
        {
          final int current = _offset;
          _offset++;
          _subscriber.onNext( _data[ current ] );
          // Subscriber can call cancel in onNext so we have to test against _offset rather than using local index
          // Should have generic test to verify this.
        }
        while ( _offset < requestEnd );

        if ( _offset == maxSize )
        {
          _subscriber.onComplete();
          done();
        }
      }
    }

    @Override
    public void cancel()
    {
      done();
    }

    private void done()
    {
      _offset = _data.length + 1;
    }

    private boolean isDone()
    {
      return _offset > _data.length;
    }
  }
}
