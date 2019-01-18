package spritz.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Mark a type as needing to be processed by SpritzProcessor.
 */
@Target( ElementType.TYPE )
public @interface MetaDataSource
{
}
