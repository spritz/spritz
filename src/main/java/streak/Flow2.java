package streak;

import javax.annotation.Nonnull;

public interface Flow2
{
  /**
   * It is unclear how we should differentiate between streams that support flow control and those that do not.
   */
  interface Upstream<T>
  {
    /**
     * Connect the downstream to the upstream.
     * This should only be invokable once?. Maybe move all the other methods into separate interface as in reactive streams API.
     */
    void connect( @Nonnull Downstream<? extends T> downstream );

    /**
     * Dispose the stream.
     * No further request are valid.
     */
    void disconnect();

    /**
     * Request upstream delivers count items.
     * This is only valid if the upstream supports flow control.
     *
     * @param count number of items to request. Must be positive (this is different from reactive-streams API)
     */
    void request( int count );

    /**
     * Spin the upstream up and start it sending events.
     * Stop event sending by calling {@link #deactivate()}
     * TODO: Maybe this method should return an enum indicating whether flow control is supported.
     */
    void activate();

    /**
     * Stop the upstream sending events and deallocate any resources associated with generating events.
     * Code can still call {@link #activate()} to have it start back up.
     */
    void deactivate();
  }

  interface Downstream<T>
  {
    void onNext( @Nonnull T item );

    /**
     * Consider this API where we can keep sending until it is accepted?
     * It is an optimization to avoid need for subsequent request(1)
     * TODO: This only relevant if there is flow control?
     */
    default boolean tryNext( @Nonnull T item )
    {
      onNext( item );
      return true;
    }

    void onError( @Nonnull Throwable throwable );

    void onComplete();
  }
}
