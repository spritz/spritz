package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.*;

/**
 * A utility class that contains reference to singleton VPU that is currently active.
 * This is extracted to a separate class to eliminate the <clinit> from {@link VirtualProcessorUnit} and thus
 * make it much easier for GWT to optimize out code based on build time compilation parameters.
 */
final class VirtualProcessorUnitCurrentHolder
{
  @Nullable
  private static VirtualProcessorUnit c_current = null;

  private VirtualProcessorUnitCurrentHolder()
  {
  }

  /**
   * Return the current VirtualProcessorUnit.
   *
   * @return the VirtualProcessorUnit.
   */
  @Nonnull
  static VirtualProcessorUnit current()
  {
    assert null != c_current;
    return c_current;
  }

  /**
   * Return true if there is a current VirtualProcessorUnit.
   *
   * @return true if there is a current VirtualProcessorUnit.
   */
  private static boolean hasCurrent()
  {
    return null != c_current;
  }

  /**
   * Set the current VirtualProcessorUnit.
   * The {@link VirtualProcessorUnit} should call this during an activation.
   *
   * @param processorUnit the VirtualProcessorUnit.
   */
  static void activate( @Nonnull final VirtualProcessorUnit processorUnit )
  {
    Objects.requireNonNull( processorUnit );
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> null != c_current,
                 () -> "Spritz-0015: Attempting set current VirtualProcessorUnit to " + processorUnit +
                       " but there is an existing  VirtualProcessorUnit activated (" + c_current + ")" );
    }
    c_current = processorUnit;
  }

  /**
   * Clear the current VirtualProcessorUnit.
   * The {@link VirtualProcessorUnit} should call this after an activation is completed.
   *
   * @param processorUnit the VirtualProcessorUnit.
   */
  static void deactivate( @Nonnull final VirtualProcessorUnit processorUnit )
  {
    Objects.requireNonNull( processorUnit );
    if ( BrainCheckConfig.checkInvariants() )
    {
      invariant( () -> processorUnit == c_current,
                 () -> "Spritz-0017: Attempting to clear current VirtualProcessorUnit from " + processorUnit +
                       " but the current VirtualProcessorUnit (" + processorUnit + ") activated does not match." );
    }
    c_current = null;
  }
}
