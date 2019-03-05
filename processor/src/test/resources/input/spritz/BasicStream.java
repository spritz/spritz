package spritz;

@MetaDataSource
public interface BasicStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default BasicStream<T> someOperator()
  {
    return null;
  }
}
