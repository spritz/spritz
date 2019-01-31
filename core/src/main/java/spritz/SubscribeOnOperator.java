package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import spritz.internal.vpu.Task;

final class SubscribeOnOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final VirtualProcessorUnit _virtualProcessorUnit;

  SubscribeOnOperator( @Nonnull final Stream<? extends T> upstream,
                       @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    super( upstream );
    _virtualProcessorUnit = Objects.requireNonNull( virtualProcessorUnit );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final Task task =
      _virtualProcessorUnit.createTask( () -> getUpstream().subscribe( new PassThroughSubscription<>( subscriber ) ),
                                        Task.Flags.DISPOSE_ON_COMPLETE );
    _virtualProcessorUnit.queueTask( task );
  }
}
