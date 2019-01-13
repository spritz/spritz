package streak;

@MetaDataSource
public interface MultipleCategoriesStream<T>
{
  @DocCategory( { DocCategory.Type.PEEKING, DocCategory.Type.TRANSFORMATION } )
  default MultipleCategoriesStream<T> anOperator()
  {
    return null;
  }
}
