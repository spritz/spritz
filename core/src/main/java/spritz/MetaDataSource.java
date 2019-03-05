package spritz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a type as needing to be processed by SpritzProcessor.
 */
@Retention( RetentionPolicy.SOURCE )
@Target( ElementType.TYPE )
@interface MetaDataSource
{
}
