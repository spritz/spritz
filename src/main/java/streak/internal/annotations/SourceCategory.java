package streak.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

/**
 * Category contains stream source factories.
 */
@Target( ElementType.FIELD )
@interface SourceCategory
{
}
