package spritz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Category contains stream source factories.
 */
@Retention( RetentionPolicy.SOURCE )
@Target( ElementType.FIELD )
@interface SourceCategory
{
}
