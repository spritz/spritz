package org.realityforge.rxs;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;

final class MapPublisher<UpstreamT, DownstreamT>
  extends AbstractPublisher<DownstreamT>
{
  @Nonnull
  private final Flow.Publisher<? extends UpstreamT> _upstream;

  @Nonnull
  private final Function<UpstreamT, DownstreamT> _transform;

  MapPublisher( @Nonnull final Flow.Publisher<? extends UpstreamT> upstream,
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
      if ( isLive() )
      {
        getDownstreamSubscriber().onNext( _transform.apply( item ) );
      }
    }
  }
}
