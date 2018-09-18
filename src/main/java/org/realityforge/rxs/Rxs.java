package org.realityforge.rxs;

public final class Rxs
{
  private Rxs()
  {
  }

  @SafeVarargs
  public static <T> Flow.Publisher<T> just( final T... values )
  {
    return new StaticPublisher<T>( values );
  }
}
