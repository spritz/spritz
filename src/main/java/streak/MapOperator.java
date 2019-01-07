package streak;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;

final class MapOperator<UpstreamT, DownstreamT>
  implements Stream<DownstreamT>
{
  @Nonnull
  private final Stream<? extends UpstreamT> _upstream;
  @Nonnull
  private final Function<UpstreamT, DownstreamT> _transform;

  MapOperator( @Nonnull final Stream<? extends UpstreamT> upstream,
               @Nonnull final Function<UpstreamT, DownstreamT> transform )
  {
    _upstream = Objects.requireNonNull( upstream );
    _transform = Objects.requireNonNull( transform );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super DownstreamT> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber, _transform ) );
  }

  private static final class WorkerSubscription<UpstreamT, DownstreamT>
    extends TransformSubscription<UpstreamT, DownstreamT>
  {
    @Nonnull
    private final Function<UpstreamT, DownstreamT> _transform;

    WorkerSubscription( @Nonnull final Subscriber<? super DownstreamT> downstreamSubscriber,
                        @Nonnull final Function<UpstreamT, DownstreamT> transform )
    {
      super( downstreamSubscriber );
      _transform = transform;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext( @Nonnull final UpstreamT item )
    {
      getDownstreamSubscriber().onNext( _transform.apply( item ) );
    }
  }
}
