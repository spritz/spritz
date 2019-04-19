package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class ScanOperator<UpstreamT, DownstreamT>
  extends AbstractStream<UpstreamT, DownstreamT>
{
  @Nonnull
  private final AccumulatorFunction<UpstreamT, DownstreamT> _accumulator;
  @Nonnull
  private final DownstreamT _initialValue;

  ScanOperator( @Nullable final String name,
                @Nonnull final Stream<UpstreamT> upstream,
                @Nonnull final AccumulatorFunction<UpstreamT, DownstreamT> accumulator,
                @Nonnull final DownstreamT initialValue )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "scan" ) : null, upstream );
    _accumulator = Objects.requireNonNull( accumulator );
    _initialValue = Objects.requireNonNull( initialValue );
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
    extends AbstractOperatorSubscription<UpstreamT, DownstreamT, ScanOperator<UpstreamT, DownstreamT>>
  {
    @Nonnull
    private DownstreamT _value;

    public WorkerSubscription( @Nonnull final ScanOperator<UpstreamT, DownstreamT> stream,
                               @Nonnull final Subscriber<? super DownstreamT> subscriber )
    {
      super( stream, subscriber );
      _value = stream._initialValue;
    }

    @Override
    public void onNext( @Nonnull final UpstreamT item )
    {
      _value = getStream()._accumulator.accumulate( item, _value );
      getSubscriber().onNext( _value );
    }
  }
}
