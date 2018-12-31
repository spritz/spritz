package streak.internal.filtering;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.StreamWithUpstream;

final class PredicateFilterStream<T>
  extends StreamWithUpstream<T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  PredicateFilterStream( @Nonnull final Flow.Stream<? extends T> upstream,
                         @Nonnull final Predicate<? super T> predicate )
  {
    super( upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _predicate ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T>
  {
    @Nonnull
    private final Predicate<? super T> _predicate;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super T> subscriber,
                        @Nonnull final Predicate<? super T> predicate )
    {
      super( subscriber );
      _predicate = Objects.requireNonNull( predicate );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      return _predicate.test( item );
    }
  }
}
