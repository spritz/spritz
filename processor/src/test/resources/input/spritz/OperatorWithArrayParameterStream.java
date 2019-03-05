package spritz;

import javax.annotation.Nonnull;

@MetaDataSource
public interface OperatorWithArrayParameterStream<T>
{
  @SuppressWarnings( "unchecked" )
  @DocCategory( DocCategory.Type.PEEKING )
  default OperatorWithArrayParameterStream<T> someOperator( @Nonnull final OperatorWithArrayParameterStream<T>... somePredicate )
  {
    return null;
  }
}
