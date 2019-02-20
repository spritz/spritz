package spritz;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

final class PredicateFilterStream<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  PredicateFilterStream( @Nonnull final Stream<T> upstream, @Nonnull final Predicate<? super T> predicate )
  {
    super( upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T, PredicateFilterStream<T>>
  {
    WorkerSubscription( @Nonnull final PredicateFilterStream<T> stream,
                        @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldIncludeItem( @Nonnull final T item )
    {
      return getStream()._predicate.test( item );
    }
  }
}
