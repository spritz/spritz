package org.realityforge.rxs;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class StaticPublisher<T>
  implements Flow.Publisher<T>
{
  private final T[] _data;
  private int _offset;

  public StaticPublisher( @Nonnull final T[] data )
  {
    _data = Objects.requireNonNull( data );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    final Flow.Subscription subscription = new Flow.Subscription()
    {
      @Override
      public void request( final int count )
      {
        final int requestEnd = _offset + count;
        final int endIndex = Math.min( requestEnd, _data.length );
        for ( int i = _offset; i < endIndex; i++ )
        {
          subscriber.onNext( _data[ i ] );
        }
        _offset = endIndex;
        if ( _offset == _data.length )
        {
          subscriber.onComplete();
        }
      }

      @Override
      public void cancel()
      {
        _offset = _data.length;
      }
    };
    subscriber.onSubscribe( new ValidatingSubscription( (ValidatingSubscriber<?>) subscriber, subscription ) );
  }
}
