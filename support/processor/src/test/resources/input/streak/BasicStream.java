package streak;

import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

@MetaDataSource
public interface BasicStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default BasicStream<T> someOperator()
  {
    return null;
  }
}
