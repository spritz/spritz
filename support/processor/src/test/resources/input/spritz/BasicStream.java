package spritz;

import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public interface BasicStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default BasicStream<T> someOperator()
  {
    return null;
  }
}
