package streak;

import javax.annotation.Nonnull;
import streak.internal.annotations.DocCategory;
import streak.internal.annotations.MetaDataSource;

@MetaDataSource
public interface OverloadedOperatorsStream<T>
{
  @FunctionalInterface
  interface MyFunction
  {
    int getTimeout();
  }

  @DocCategory( DocCategory.Type.PEEKING )
  default OverloadedOperatorsStream<T> operator( @Nonnull final OverloadedOperatorsStream<T> timeoutControlStream )
  {
    return null;
  }

  @DocCategory( DocCategory.Type.PEEKING )
  default OverloadedOperatorsStream<T> operator( final int timeout )
  {
    return null;
  }

  @DocCategory( DocCategory.Type.PEEKING )
  default OverloadedOperatorsStream<T> operator( @Nonnull final MyFunction timeoutFn )
  {
    return null;
  }
}
