package streak;

import javax.annotation.Nonnull;
import streak.internal.PublisherWithUpstream;

final class ValidatingPublisher<T>
  extends PublisherWithUpstream<T>
{
  ValidatingPublisher( @Nonnull final Flow.Stream<? extends T> upstream )
  {
    super( upstream );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new ValidatingSubscriber<>( subscriber ) );
  }
}
