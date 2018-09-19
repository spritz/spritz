package org.realityforge.rxs;

import javax.annotation.Nonnull;

/**
 * This is re-implements the API added to java 9 which is a reimplementation of the reactive streams API with
 * the exception that the long was converted to an int to reduce overhead when compiling to js. We also assume that
 * {@link Subscription#request(int)} is passed a positive count.
 */
public final class Flow
{
  private Flow()
  {
  }

  public interface Subscription
  {
    /**
     * @param count number of items to request. Must be positive (this is different from reactive-streams API)
     */
    void request( int count );

    void cancel();
  }

  public interface Subscriber<T>
  {
    void onSubscribe( @Nonnull Subscription subscription );

    void onNext( @Nonnull T item );

    void onError( @Nonnull Throwable throwable );

    void onComplete();
  }

  @FunctionalInterface
  public interface Publisher<T>
  {
    void subscribe( @Nonnull Subscriber<? super T> subscriber );
  }

  public interface Processor<P, S>
    extends Publisher<P>, Subscriber<S>
  {
  }
}
