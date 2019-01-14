package streak;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

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
