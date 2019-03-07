package spritz;

import javax.annotation.Nonnull;

public interface SourceCreator<T>
{
  void create( @Nonnull EventEmitter<T> observer );
}
