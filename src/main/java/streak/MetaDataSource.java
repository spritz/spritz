package streak;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Mark a type as needing to be processed by StreakProcessor.
 */
@Target( ElementType.TYPE )
@interface MetaDataSource
{
}
