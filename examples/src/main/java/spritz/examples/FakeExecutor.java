package spritz.examples;

import spritz.VirtualProcessorUnit;
import spritz.internal.vpu.RoundBasedTaskExecutor;

class FakeExecutor
{
  static VirtualProcessorUnit VPU1 = new VirtualProcessorUnit( new RoundBasedTaskExecutor() );
  static VirtualProcessorUnit VPU2 = new VirtualProcessorUnit( new RoundBasedTaskExecutor() );
  static VirtualProcessorUnit VPU3 = new VirtualProcessorUnit( new RoundBasedTaskExecutor() );
  static VirtualProcessorUnit VPU4 = new VirtualProcessorUnit( new RoundBasedTaskExecutor() );
}
