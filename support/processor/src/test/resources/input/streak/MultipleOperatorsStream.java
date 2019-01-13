package streak;

@MetaDataSource
public interface MultipleOperatorsStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default MultipleOperatorsStream<T> operatorZ()
  {
    return null;
  }

  @DocCategory( DocCategory.Type.PEEKING )
  default MultipleOperatorsStream<T> operatorP()
  {
    return null;
  }

  @DocCategory( DocCategory.Type.PEEKING )
  default MultipleOperatorsStream<T> operatorA()
  {
    return null;
  }

  @DocCategory( DocCategory.Type.PEEKING )
  default MultipleOperatorsStream<T> operatorQ()
  {
    return null;
  }
}
