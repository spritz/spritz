package spritz;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import spritz.internal.annotations.DocCategory;
import spritz.internal.annotations.MetaDataSource;

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
