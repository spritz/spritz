package streak;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface AccumulatorFunction<Input, Output>
{
  @Nonnull
  Output accumulate( @Nonnull Input item, @Nonnull Output accumulatedValue );
}
