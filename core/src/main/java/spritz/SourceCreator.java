package spritz;

import javax.annotation.Nonnull;

public interface SourceCreator<T>
{
  void create( @Nonnull Observer<T> subscriber );

  interface Observer<T>
    extends EventEmitter<T>
  {
    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    boolean isCancelled();
  }
}
