package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    final VirtualProcessorUnit current = CurrentVPU.current();
    return null != current ? current : direct();
  }

  /**
   * Set the current VirtualProcessorUnit.
   * The {@link VirtualProcessorUnit} should call this during an activation.
   *
   * @param processorUnit the VirtualProcessorUnit.
   */
  static void activate( @Nonnull final VirtualProcessorUnit processorUnit )
  {
    CurrentVPU.activate( processorUnit );
  }

  /**
   * Clear the current VirtualProcessorUnit.
   * The {@link VirtualProcessorUnit} should call this after an activation is completed.
   *
   * @param processorUnit the VirtualProcessorUnit.
   */
  static void deactivate( @Nonnull final VirtualProcessorUnit processorUnit )
  {
    CurrentVPU.deactivate( processorUnit );
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
  private static VirtualProcessorUnit direct()
  {
    return DirectVPU.VPU;
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
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( Spritz.areNamesEnabled() ? "macro" : null, new MacroTaskExecutor() );
  }

  private static final class MicroTaskVPU
  {
    private MicroTaskVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( Spritz.areNamesEnabled() ? "micro" : null,
                                SpritzConfig.isJvm() ? new MacroTaskExecutor() : new MicroTaskExecutor() );
  }

  private static final class AnimationFrameVPU
  {
    private AnimationFrameVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( Spritz.areNamesEnabled() ? "animationFrame" : null,
                                SpritzConfig.isJvm() ? new MacroTaskExecutor() : new AnimationFrameExecutor() );
  }

  private static final class AfterFrameVPU
  {
    private AfterFrameVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( Spritz.areNamesEnabled() ? "afterFrame" : null,
                                SpritzConfig.isJvm() ? new MacroTaskExecutor() : new AfterFrameExecutor() );
  }

  private static final class DirectVPU
  {
    private DirectVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( Spritz.areNamesEnabled() ? "direct" : null, new DirectExecutor() );
  }

  private static final class OnIdleVPU
  {
    private OnIdleVPU()
    {
    }

    @Nonnull
    private static final VirtualProcessorUnit VPU =
      new VirtualProcessorUnit( Spritz.areNamesEnabled() ? "onIdle" : null,
                                SpritzConfig.isJvm() ? new MacroTaskExecutor() : new OnIdleExecutor() );
  }

  /**
   * A utility class that contains reference to singleton VPU that is currently active.
   */
  private static final class CurrentVPU
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
    @Nullable
    private static VirtualProcessorUnit current()
    {
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
      if ( Spritz.shouldCheckInvariants() )
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
      if ( Spritz.shouldCheckInvariants() )
      {
        invariant( () -> processorUnit == c_current,
                   () -> "Spritz-0017: Attempting to clear current VirtualProcessorUnit from " + processorUnit +
                         " but the current VirtualProcessorUnit (" + processorUnit + ") activated does not match." );
      }
      c_current = null;
    }
  }
}
