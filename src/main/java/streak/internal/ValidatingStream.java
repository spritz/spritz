package streak.internal;

import javax.annotation.Nonnull;
import streak.Flow;

final class ValidatingStream<T>
  extends StreamWithUpstream<T>
{
  ValidatingStream( @Nonnull final Flow.Stream<? extends T> upstream )
  {
    super( upstream );
  }

  @Override
  public void subscribe( @Nonnull final Flow.Subscriber<? super T> subscriber )
  {
    getUpstream().subscribe( new ValidatingSubscriber<>( subscriber ) );
  }
}
