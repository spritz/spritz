package spritz.internal.vpu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A utility class that contains reference to singleton VPU that is currently active.
 * This is extracted to a separate class to eliminate the <clinit> from {@link VirtualProcessorUnit} and thus
 * make it much easier for GWT to optimize out code based on build time compilation parameters.
 */
final class VirtualProcessorUnitHolder
{
  @Nullable
  private static VirtualProcessorUnit c_current = null;

  private VirtualProcessorUnitHolder()
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
   * @param current the current VirtualProcessorUnit.
   */
  static void setCurrent( @Nullable final VirtualProcessorUnit current )
  {
    c_current = current;
  }
}
