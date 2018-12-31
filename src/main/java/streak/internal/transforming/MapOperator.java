package streak.internal.transforming;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class MapOperator<UpstreamT, DownstreamT>
  extends AbstractStream<DownstreamT>
{
  @Nonnull
  private final Flow.Stream<? extends UpstreamT> _upstream;
  @Nonnull
  private final Function<UpstreamT, DownstreamT> _transform;

  MapOperator( @Nonnull final Flow.Stream<? extends UpstreamT> upstream,
               @Nonnull final Function<UpstreamT, DownstreamT> transform )
  {
    _upstream = Objects.requireNonNull( upstream );
    _transform = Objects.requireNonNull( transform );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super DownstreamT> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber, _transform ) );
  }

  private static final class WorkerSubscription<UpstreamT, DownstreamT>
    extends TransformSubscription<UpstreamT, DownstreamT>
  {
    @Nonnull
    private final Function<UpstreamT, DownstreamT> _transform;

    WorkerSubscription( @Nonnull final Flow.Subscriber<? super DownstreamT> downstreamSubscriber,
                        @Nonnull final Function<UpstreamT, DownstreamT> transform )
    {
      super( downstreamSubscriber );
      _transform = transform;
    }

    /**
     * {@inheritDoc}
     */
    public void onNext( @Nonnull final UpstreamT item )
    {
      assert isNotDisposed();
      getDownstreamSubscriber().onNext( _transform.apply( item ) );
    }
  }
}
