package org.realityforge.rxs;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

public interface PublisherExtension<T>
{
  default Flow.Publisher<T> filter( @Nonnull final Predicate<T> predicate )
  {
    return new PredicateFilterPublisher<>( self(), predicate );
  }

  @Nonnull
  Flow.Publisher<T> self();
}
