package org.realityforge.rxs;

import javax.annotation.Nonnull;

public interface PublisherExtension<T>
{
  @Nonnull
  Flow.Publisher<T> self();
}
