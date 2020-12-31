package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import zemeckis.VirtualProcessorUnit;

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

  @Nonnull
  @Override
  Subscription doSubscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    final PassThroughSubscription<T, SubscribeOnOperator<T>> subscription =
      new PassThroughSubscription<>( this, subscriber );
    _virtualProcessorUnit.queue( () -> getUpstream().subscribe( subscription ) );
    return subscription;
  }
}
