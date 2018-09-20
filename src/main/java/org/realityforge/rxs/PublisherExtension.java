package org.realityforge.rxs;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface PublisherExtension<T>
{
  default Flow.Publisher<T> filter( @Nonnull final Predicate<T> predicate )
  {
    return new PredicateFilterPublisher<>( self(), predicate );
  }

  default Flow.Publisher<T> take( final int count )
  {
    return new TakeFilterPublisher<>( self(), count );
  }

  default Flow.Publisher<T> first()
  {
    return take( 1 );
  }

  default Flow.Publisher<T> skipUntil( @Nonnull final Predicate<T> predicate )
  {
    return new SkipUntilPredicateFilterPublisher<>( self(), predicate );
  }

  @Nonnull
  Flow.Publisher<T> self();
}
