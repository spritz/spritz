package spritz;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class PredicateFilterStream<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  PredicateFilterStream( @Nullable final String name,
                         @Nonnull final Stream<T> upstream,
                         @Nonnull final Predicate<? super T> predicate )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "filter" ) : null, upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final WorkerSubscription<T> subscription = new WorkerSubscription<>( this, subscriber );
    getUpstream().subscribe( subscription );
    return subscription;
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
    boolean shouldIncludeItem( @Nonnull final T item )
    {
      return getStream()._predicate.test( item );
    }
  }
}
