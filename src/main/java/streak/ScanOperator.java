package streak;

import java.util.Objects;
import javax.annotation.Nonnull;

final class ScanOperator<UpstreamT, DownstreamT>
  extends AbstractStream<DownstreamT>
{
  @Nonnull
  private final Stream<? extends UpstreamT> _upstream;
  @Nonnull
  private final AccumulatorFunction<UpstreamT, DownstreamT> _accumulator;
  @Nonnull
  private final DownstreamT _initialValue;

  ScanOperator( @Nonnull final Stream<? extends UpstreamT> upstream,
                @Nonnull final AccumulatorFunction<UpstreamT, DownstreamT> accumulator,
                @Nonnull final DownstreamT initialValue )
  {
    _upstream = Objects.requireNonNull( upstream );
    _accumulator = Objects.requireNonNull( accumulator );
    _initialValue = Objects.requireNonNull( initialValue );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super DownstreamT> subscriber )
  {
    _upstream.subscribe( new WorkerSubscription<>( subscriber, _accumulator, _initialValue ) );
  }

  private static final class WorkerSubscription<UpstreamT, DownstreamT>
    extends TransformSubscription<UpstreamT, DownstreamT>
  {
    @Nonnull
    private final AccumulatorFunction<UpstreamT, DownstreamT> _accumulator;
    @Nonnull
    private DownstreamT _value;

    WorkerSubscription( @Nonnull final Subscriber<? super DownstreamT> downstreamSubscriber,
                        @Nonnull final AccumulatorFunction<UpstreamT, DownstreamT> accumulator,
                        @Nonnull final DownstreamT initialValue )
    {
      super( downstreamSubscriber );
      _accumulator = accumulator;
      _value = initialValue;
    }

    /**
     * {@inheritDoc}
     */
    public void onNext( @Nonnull final UpstreamT item )
    {
      _value = _accumulator.accumulate( item, _value );
      getDownstreamSubscriber().onNext( _value );
    }
  }
}
