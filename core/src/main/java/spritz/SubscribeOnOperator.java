package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

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
      _virtualProcessorUnit.task( () -> getUpstream().subscribe( new PassThroughSubscription<>( subscriber ) ) );
    _virtualProcessorUnit.queue( task );
  }
}