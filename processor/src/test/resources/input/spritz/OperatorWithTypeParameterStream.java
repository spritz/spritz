package spritz;

import javax.annotation.Nonnull;

@MetaDataSource
public interface OperatorWithTypeParameterStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default OperatorWithTypeParameterStream<T> of( @Nonnull final T value )
  {
    return null;
  }
}
