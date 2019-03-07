package spritz;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class TakeWhileOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  TakeWhileOperator( @Nullable final String name,
                     @Nonnull final Stream<T> upstream,
                     @Nonnull final Predicate<? super T> predicate )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "takeWhile" ) : null, upstream );
    _predicate = Objects.requireNonNull( predicate );
  }

  @Override
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractFilterSubscription<T, TakeWhileOperator<T>>
  {
    WorkerSubscription( @Nonnull final TakeWhileOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( getStream()._predicate.test( item ) )
      {
        return true;
      }
      else
      {
        getUpstream().cancel();
        onComplete();
        return false;
      }
    }
  }
}
