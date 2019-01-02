package streak.internal.transforming;

import java.util.Objects;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.internal.AbstractStream;

final class FlattenOperator<DownstreamT>
  extends AbstractStream<DownstreamT>
{
  @Nonnull
  private final Flow.Stream<Flow.Stream<DownstreamT>> _upstream;

  FlattenOperator( @Nonnull final Flow.Stream<Flow.Stream<DownstreamT>> upstream )
  {
    _upstream = Objects.requireNonNull( upstream );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super DownstreamT> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber ) );
  }

  private static final class WorkerSubscription<DownstreamT>
    extends TransformSubscription<Flow.Stream<DownstreamT>, DownstreamT>
  {
    WorkerSubscription( @Nonnull final Flow.Subscriber<? super DownstreamT> downstreamSubscriber )
    {
      super( downstreamSubscriber );
    }

    /**
     * {@inheritDoc}
     */
    public void onNext( @Nonnull final Flow.Stream<DownstreamT> item )
    {
    }
  }
}
