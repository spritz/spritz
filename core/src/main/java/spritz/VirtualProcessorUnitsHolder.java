package spritz;

import javax.annotation.Nonnull;

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
}
