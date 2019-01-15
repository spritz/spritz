package streak;

import javax.annotation.Nonnull;
import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

@MetaDataSource
public interface OperatorWithArrayParameterStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default OperatorWithArrayParameterStream<T> someOperator( @Nonnull final OperatorWithArrayParameterStream<T>... somePredicate )
  {
    return null;
  }
}
