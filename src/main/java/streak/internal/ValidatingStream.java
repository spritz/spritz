package streak.internal;

import javax.annotation.Nonnull;
import streak.Stream;
import streak.Subscriber;

final class ValidatingStream<T>
  extends StreamWithUpstream<T>
{
  ValidatingStream( @Nonnull final Stream<? extends T> upstream )
  {
    super( upstream );
  }

  @Override
  public void subscribe( @Nonnull final Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new ValidatingSubscriber<>( subscriber ) );
  }
}
