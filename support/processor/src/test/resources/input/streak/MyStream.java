package streak;

@MetaDataSource
public interface MyStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default MyStream<T> someOperator()
  {
    return null;
  }
}
