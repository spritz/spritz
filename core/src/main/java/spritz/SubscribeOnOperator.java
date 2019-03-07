package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class SubscribeOnOperator<T>
  extends AbstractStream<T, T>
{
  @Nonnull
  private final VirtualProcessorUnit _virtualProcessorUnit;

  SubscribeOnOperator( @Nullable final String name,
                       @Nonnull final Stream<T> upstream,
                       @Nonnull final VirtualProcessorUnit virtualProcessorUnit )
  {
    super( Spritz.areNamesEnabled() ? generateName( name, "subscribeOn", virtualProcessorUnit.getName() ) : null,
           upstream );
    _virtualProcessorUnit = Objects.requireNonNull( virtualProcessorUnit );
  }

  @Override
  void doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    _virtualProcessorUnit.getExecutor()
      .queue( () -> getUpstream().subscribe( new PassThroughSubscription<>( this, subscriber ) ) );
  }
}
