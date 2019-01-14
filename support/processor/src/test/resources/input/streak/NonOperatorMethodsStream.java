package streak;

import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

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
