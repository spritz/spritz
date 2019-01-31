package spritz.internal.vpu;

import javax.annotation.Nonnull;
import spritz.VirtualProcessorUnit;

/**
 * Interface representing {@link VirtualProcessorUnit} passed to {@link TaskExecutor} during initialization.
 * This interface is designed to allow the {@link TaskExecutor} to activate the {@link VirtualProcessorUnit}
 * when it needs to execute tasks.
 */
public interface ExecutorContext
{
  @FunctionalInterface
  interface ActivationFn
  {
    /**
     * Callback method invoked by {@link ExecutorContext#activate(ActivationFn)} to process tasks.
     */
    void invoke();
  }

  /**
   * Activate the associated {@link VirtualProcessorUnit}.
   * This method MUST only be called if there is no {@link VirtualProcessorUnit} unit currently activated.
   * The activation will set the {@link VirtualProcessorUnit#current()} for the duration
   * of the activation and invoke {@link ActivationFn#invoke()} passed into the method..
   */
  void activate( @Nonnull ActivationFn activationFn );
}
