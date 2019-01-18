package spritz;

import javax.annotation.Nonnull;
import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public interface OperatorWithTypeParameterStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default OperatorWithTypeParameterStream<T> of( @Nonnull final T value )
  {
    return null;
  }
}
