package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Abstract subscription implementation for the common scenario where
 * there is an upstream stage and associated subscription.
 */
abstract class AbstractSubscription<T, S extends Stream<T>>
  extends AbstractBaseSubscription<T>
{
  /**
   * The stream from which this subscription was created.
   */
  @Nonnull
  private final S _stream;

  AbstractSubscription( @Nonnull final S stream, @Nonnull final Subscriber<? super T> subscriber )
  {
    super( subscriber );
    _stream = Objects.requireNonNull( stream );
  }

  @Nonnull
  final S getStream()
  {
    return _stream;
  }

  @Override
  protected final String getQualifiedName()
  {
    return getStream().getQualifiedName();
  }
}
