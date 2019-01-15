package streak;

import javax.annotation.Nonnull;
import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

@MetaDataSource
public interface OperatorWithTypeParameterStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default OperatorWithTypeParameterStream<T> of( @Nonnull final T value )
  {
    return null;
  }
}
