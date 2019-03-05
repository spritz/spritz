package spritz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Category contains stream source factories.
 */
@Target( ElementType.FIELD )
@interface SourceCategory
{
}
