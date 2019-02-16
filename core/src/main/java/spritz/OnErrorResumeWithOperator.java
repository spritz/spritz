package spritz;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;

final class OnErrorResumeWithOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final Function<Throwable, Stream<T>> _streamFromErrorFn;

  OnErrorResumeWithOperator( @Nonnull final Publisher<T> upstream,
                             @Nonnull final Function<Throwable, Stream<T>> streamFromErrorFn )
  {
    super( upstream );
    _streamFromErrorFn = Objects.requireNonNull( streamFromErrorFn );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( subscriber, _streamFromErrorFn ) );
  }

  private static final class WorkerSubscription<T>
    extends AbstractOperatorSubscription<T>
  {
    @Nonnull
    private final Function<Throwable, Stream<T>> _streamFromErrorFn;
    private boolean _downstreamSubscribed;

    WorkerSubscription( @Nonnull final Subscriber<? super T> subscriber,
                        @Nonnull final Function<Throwable, Stream<T>> streamFromErrorFn )
    {
      super( subscriber );
      _streamFromErrorFn = streamFromErrorFn;
    }

    @Override
    public void onSubscribe( @Nonnull final Subscription subscription )
    {
      if ( _downstreamSubscribed )
      {
        setUpstream( subscription );
      }
      else
      {
        _downstreamSubscribed = true;
        super.onSubscribe( subscription );
      }
    }

    @Override
    public void onError( @Nonnull final Throwable error )
    {
      try
      {
        final Stream<T> nextStream = _streamFromErrorFn.apply( error );
        if ( null != nextStream )
        {
          nextStream.subscribe( this );
        }
        else
        {
          super.onError( error );
        }
      }
      catch ( final Exception e )
      {
        super.onError( error );
      }
    }
  }
}
