package org.realityforge.rxs;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class StaticPublisher<T>
  implements Flow.Publisher<T>
{
  private final T[] _data;

  StaticPublisher( @Nonnull final T[] data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    subscriber.onSubscribe( new StaticSubscription( subscriber ) );
  }

  private class StaticSubscription
    implements Flow.Subscription
  {
    private final Flow.Subscriber<? super T> _subscriber;
    private int _offset;

    StaticSubscription( final Flow.Subscriber<? super T> subscriber )
    {
      _subscriber = subscriber;
    }

    @Override
    public void request( final int count )
    {
      final int requestEnd = _offset + count;
      if ( requestEnd <= _data.length )
      {
        for ( int i = _offset; i < requestEnd; i++ )
        {
          _subscriber.onNext( _data[ i ] );
        }
        _offset = requestEnd;
        if ( _offset == _data.length )
        {
          _subscriber.onComplete();
          _offset = _data.length + 1;
        }
      }
    }

    @Override
    public void cancel()
    {
      if ( _offset <= _data.length )
      {
        _subscriber.onComplete();
        _offset = _data.length + 1;
      }
    }
  }
}
