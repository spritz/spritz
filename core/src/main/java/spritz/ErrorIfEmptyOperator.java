package spritz;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

final class ErrorIfEmptyOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final Supplier<Throwable> _errorFactory;

  ErrorIfEmptyOperator( @Nonnull final Stream<T> upstream, @Nonnull final Supplier<Throwable> errorFactory )
  {
    super( upstream );
    _errorFactory = Objects.requireNonNull( errorFactory );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<T>
    extends PassThroughSubscription<T, ErrorIfEmptyOperator<T>>
  {
    private boolean _itemEmitted;

    WorkerSubscription( @Nonnull final ErrorIfEmptyOperator<T> stream, @Nonnull final Subscriber<? super T> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    public void onComplete()
    {
      if ( !_itemEmitted )
      {
        super.onError( getStream()._errorFactory.get() );
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
