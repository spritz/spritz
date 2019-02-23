package spritz;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class MapOperator<UpstreamT, DownstreamT>
  extends AbstractStream<UpstreamT, DownstreamT>
{
  @Nonnull
  private final Function<UpstreamT, DownstreamT> _transform;

  MapOperator( @Nullable final String name,
               @Nonnull final Stream<UpstreamT> upstream,
               @Nonnull final Function<UpstreamT, DownstreamT> transform )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "map" ) : null, upstream );
    _transform = Objects.requireNonNull( transform );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super DownstreamT> subscriber )
  {
    getUpstream().subscribe( new WorkerSubscription<>( this, subscriber ) );
  }

  private static final class WorkerSubscription<UpstreamT, DownstreamT>
    extends AbstractOperatorSubscription<UpstreamT, DownstreamT, MapOperator<UpstreamT, DownstreamT>>
  {
    WorkerSubscription( @Nonnull final MapOperator<UpstreamT, DownstreamT> stream,
                        @Nonnull final Subscriber<? super DownstreamT> subscriber )
    {
      super( stream, subscriber );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNext( @Nonnull final UpstreamT item )
    {
      getSubscriber().onNext( getStream()._transform.apply( item ) );
    }
  }
}
