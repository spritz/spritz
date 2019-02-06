package spritz.examples;

import spritz.VirtualProcessorUnit;
import spritz.internal.vpu.MacroTaskExecutor;

class FakeExecutor
{
  static VirtualProcessorUnit VPU1 = new VirtualProcessorUnit( new MacroTaskExecutor() );
  static VirtualProcessorUnit VPU2 = new VirtualProcessorUnit( new MacroTaskExecutor() );
  static VirtualProcessorUnit VPU3 = new VirtualProcessorUnit( new MacroTaskExecutor() );
  static VirtualProcessorUnit VPU4 = new VirtualProcessorUnit( new MacroTaskExecutor() );
}
