package spritz;

import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

@MetaDataSource
public interface MultipleCategoriesStream<T>
{
  @DocCategory( { DocCategory.Type.PEEKING, DocCategory.Type.TRANSFORMATION } )
  default MultipleCategoriesStream<T> anOperator()
  {
    return null;
  }
}
