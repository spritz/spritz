package streak;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

@MetaDataSource
public interface OperatorWithParametersStream<T>
{
  @DocCategory( DocCategory.Type.PEEKING )
  default OperatorWithParametersStream<T> someOperator( final int someNumber,
                                                        @Nonnull final Predicate<T> somePredicate )
  {
    return null;
  }
}
