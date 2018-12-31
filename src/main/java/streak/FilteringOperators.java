package streak;

public interface FilteringOperators<UpstreamT, R extends FilteringOperators<UpstreamT, R>>
{
  /**
   * Creates a stream consisting of the distinct elements (according to {@link Object#equals(Object)}) of this stream.
   * <p>
   * <img src="doc-files/distinct.png" alt="distinct marbles diagram">
   *
   * @return A new stream builder emitting the distinct elements from this stream.
   */
  R distinct();
}
