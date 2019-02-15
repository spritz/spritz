package spritz;

import javax.annotation.Nonnull;

public interface EventEmitter<T>
{
  void next( @Nonnull T item );

  void error( @Nonnull Throwable error );

  void complete();
}
