package streak;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

final class ErrorIfEmptyOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Supplier<Throwable> _errorFactory;

  ErrorIfEmptyOperator( @Nonnull final Stream<? extends T> upstream, @Nonnull final Supplier<Throwable> errorFactory )
  {
    super( upstream );
    _errorFactory = Objects.requireNonNull( errorFactory );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _errorFactory ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<T>
  {
    @Nonnull
    private final Supplier<Throwable> _errorFactory;
    private boolean _itemEmitted;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        @Nonnull final Supplier<Throwable> errorFactory )
    {
      super( subscriber );
      _errorFactory = errorFactory;
    }

    @Override
    public void onComplete()
    {
      if ( !_itemEmitted )
      {
        super.onError( _errorFactory.get() );
      }
      else
      {
        super.onComplete();
      }
    }

    @Override
    public void onNext( @Nonnull final T item )
    {
      _itemEmitted = true;
      super.onNext( item );
    }
  }
}
