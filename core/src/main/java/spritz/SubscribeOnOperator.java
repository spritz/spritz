package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

final class SubscribeOnOperator<T>
  extends AbstractStream<T>
{
  @Nonnull
  private final VirtualProcessorUnit _virtualProcessorUnit;

  SubscribeOnOperator( @Nonnull final Publisher<T> upstream, @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    super( upstream );
    _virtualProcessorUnit = Objects.requireNonNull( virtualProcessorUnit );
  }

  @Override
  protected void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _virtualProcessorUnit.queue( () -> getUpstream().subscribe( new PassThroughSubscription<>( subscriber ) ) );
  }
}
