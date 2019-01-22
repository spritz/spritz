package spritz;

import javax.annotation.Nonnull;

public interface SourceCreator<T>
{
  void create( @Nonnull Observer<T> subscriber );

  interface Observer<T>
  {
    void next( @Nonnull T item );

    void error( @Nonnull Throwable throwable );

    void complete();

    @SuppressWarnings( "BooleanMethodIsAlwaysInverted" )
    boolean isCancelled();
  }
}
