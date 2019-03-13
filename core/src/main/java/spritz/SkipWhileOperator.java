package spritz;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class SkipWhileOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Predicate<? super T> _predicate;

  SkipWhileOperator( @Nullable final String name,
                     @Nonnull final Stream<T> upstream,
                     @Nonnull final Predicate<? super T> predicate )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "skipWhile" ) : null, upstream );
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
    extends AbstractFilterSubscription<T, SkipWhileOperator<T>>
  {
    private boolean _allow;

    WorkerSubscription( @Nonnull final SkipWhileOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    boolean shouldIncludeItem( @Nonnull final T item )
    {
      if ( _allow )
      {
        return true;
      }
      else if ( !getStream()._predicate.test( item ) )
      {
        _allow = true;
        return true;
      }
      else
      {
        return false;
      }
    }
  }
}
