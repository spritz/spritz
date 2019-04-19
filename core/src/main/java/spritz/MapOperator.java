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

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super DownstreamT> subscriber )
  {
    final WorkerSubscription<UpstreamT, DownstreamT> subscription = new WorkerSubscription<>( this, subscriber );
    getUpstream().subscribe( subscription );
    return subscription;
  }

  private static final class WorkerSubscription<UpstreamT, DownstreamT>
    extends AbstractOperatorSubscription<UpstreamT, DownstreamT, MapOperator<UpstreamT, DownstreamT>>
  {
    WorkerSubscription( @Nonnull final MapOperator<UpstreamT, DownstreamT> stream,
                        @Nonnull final Subscriber<? super DownstreamT> subscriber )
    {
      super( stream, subscriber );
    }

    @Override
    public void onNext( @Nonnull final UpstreamT item )
    {
      getSubscriber().onNext( getStream()._transform.apply( item ) );
    }
  }
}
