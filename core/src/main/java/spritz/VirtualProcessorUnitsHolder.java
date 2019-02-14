package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.realityforge.braincheck.BrainCheckConfig;
import static org.realityforge.braincheck.Guards.*;

/**
 * A container that holds separate inner classes for each VPU that Spritz supports.
 * The whole purpose of this dance is to avoid the creation of &lt;clinit&gt; sections
 * to improve code optimizers chances of dead code removal.
 */
final class VirtualProcessorUnitsHolder
{
  private VirtualProcessorUnitsHolder()
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
    return CurrentVPU.current();
  }

  @Nonnull
  static VirtualProcessorUnit macroTask()
  {
    return MacroTaskVPU.VPU;
  }

  @Nonnull
  static VirtualProcessorUnit microTask()
  {
    return MicroTaskVPU.VPU;
  }

  @Nonnull
  static VirtualProcessorUnit animationFrame()
  {
    return AnimationFrameVPU.VPU;
  }

  @Nonnull
  static VirtualProcessorUnit afterFrame()
  {
    return AfterFrameVPU.VPU;
  }

  @Nonnull
  static VirtualProcessorUnit onIdle()
  {
    return OnIdleVPU.VPU;
  }

  private static final class MacroTaskVPU
  {
    private MacroTaskVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU = new VirtualProcessorUnit( new MacroTaskExecutor() );
  }

  private static final class MicroTaskVPU
  {
    private MicroTaskVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( SpritzConfig.isJvm() ? new MacroTaskExecutor() : new MicroTaskExecutor() );
  }

  private static final class AnimationFrameVPU
  {
    private AnimationFrameVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( SpritzConfig.isJvm() ? new MacroTaskExecutor() : new AnimationFrameExecutor() );
  }

  private static final class AfterFrameVPU
  {
    private AfterFrameVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( SpritzConfig.isJvm() ? new MacroTaskExecutor() : new AfterFrameExecutor() );
  }

  private static final class OnIdleVPU
  {
    private OnIdleVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( SpritzConfig.isJvm() ? new MacroTaskExecutor() : new OnIdleExecutor() );
  }

  /**
   * A utility class that contains reference to singleton VPU that is currently active.
   */
  static final class CurrentVPU
  {
    @Nullable
    private static VirtualProcessorUnit c_current = null;

    private CurrentVPU()
    {
    }

    /**
     * Return the current VirtualProcessorUnit.
     *
     * @return the VirtualProcessorUnit.
     */
    @Nonnull
    private static VirtualProcessorUnit current()
    {
      assert null != c_current;
      return c_current;
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
        invariant( () -> null == c_current,
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
}
