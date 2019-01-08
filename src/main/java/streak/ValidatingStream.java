package streak;

import javax.annotation.Nonnull;

final class ValidatingStream<T>
  extends AbstractStream<T>
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
