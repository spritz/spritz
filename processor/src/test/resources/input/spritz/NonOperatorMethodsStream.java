package spritz;

import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public interface NonOperatorMethodsStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default NonOperatorMethodsStream<T> operator( final int timeout )
  {
    return null;
  }

  default void someOtherRandomMethod()
  {
  }
}
