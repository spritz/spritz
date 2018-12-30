package streak;

import java.util.function.Predicate;

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

  /**
   * Take the longest prefix of elements from this stream that satisfy the given {@code predicate}.
   * <p>
   * <img src="doc-files/takeWhile.png" alt="takeWhile marble diagram">
   * <p>
   * When the {@code predicate} returns false, the stream will be completed, and upstream will be cancelled.
   *
   * @param predicate The predicate.
   * @return A new stream builder.
   */
  R takeWhile( Predicate<? super UpstreamT> predicate );
}
