package streak;

import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

@MetaDataSource
public interface MultipleCategoriesStream<T>
{
  @DocCategory( { DocCategory.Type.PEEKING, DocCategory.Type.TRANSFORMATION } )
  default MultipleCategoriesStream<T> anOperator()
  {
    return null;
  }
}
