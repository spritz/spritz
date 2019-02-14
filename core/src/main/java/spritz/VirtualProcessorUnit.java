package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Processing unit responsible for executing tasks.
 */
public final class VirtualProcessorUnit
{
  /**
   * The executor responsible for selecting and invoking tasks.
   */
  @Nonnull
  private final Executor _executor;

  /**
   * Create the processor unit.
   *
   * @param executor the associated task executor.
   */
  VirtualProcessorUnit( @Nonnull final Executor executor )
  {
    _executor = Objects.requireNonNull( executor );
    _executor.init( this::activate );
  }

  /**
   * Queue the task to execute on the current VPU.
   * This method MUST NOT be invoked if there is no {@link VirtualProcessorUnit} activated.
   *
   * @param task the task.
   */
  public static void current( @Nonnull final Runnable task )
  {
    VirtualProcessorUnitsHolder.current().queue( task );
  }

  /**
   * Queue the task to execute in the next "macro" task.
   * This is the default VPU in the browser and indicates tasks that are scheduled via a
   * call to <code>setTimeout(callback,0)</code>.
   *
   * @param task the task.
   */
  public static void macroTask( @Nonnull final Runnable task )
  {
    VirtualProcessorUnitsHolder.macroTask().queue( task );
  }

  /**
   * Queue the task to execute in the next "micro" task.
   * The "micro" tasks are those that the browser executes after the current "macro" or "micro" task.
   * This VPU schedules an activation via a call to {@code Promise.resolve().then( v -> callback() )}.
   *
   * @param task the task.
   */
  public static void microTask( @Nonnull final Runnable task )
  {
    VirtualProcessorUnitsHolder.microTask().queue( task );
  }

  /**
   * Queue the task to execute in the next "animationFrame".
   * The "animationFrame" occurs prior to the next render frame.
   * This VPU schedules an activation via a call to <code>requestAnimationFrame( callback )</code>.
   *
   * @param task the task.
   */
  public static void animationFrame( @Nonnull final Runnable task )
  {
    VirtualProcessorUnitsHolder.animationFrame().queue( task );
  }

  /**
   * Queue the task to execute after the next browser render.
   * The "afterFrame" tasks are invoked after the next frames render by responding to a message on a
   * MessageChannel that is sent in {@code requestAnimationFrame()}.
   *
   * @param task the task.
   */
  public static void afterFrame( @Nonnull final Runnable task )
  {
    VirtualProcessorUnitsHolder.afterFrame().queue( task );
  }

  /**
   * Queue the task to execute when the browser is idle.
   * The browser activates the onIdle VirtualProcessorUnit when idle and will pass the
   * duration for which the VPU may run. The VPU will execute tasks as long as there is tasks
   * queued and the deadline has not been reached after which the VPU will return control to the
   * browser. Unlike other VPUs, when the onIdle VirtualProcessorUnit completes the activation, there
   * may still be tasks in the queue and if there is the VPU will re-schedule itself for another activation.
   * This VPU schedules an activation via a call to <code>requestIdleCallback( callback )</code>.
   *
   * @param task the task.
   */
  public static void onIdle( @Nonnull final Runnable task )
  {
    VirtualProcessorUnitsHolder.onIdle().queue( task );
  }

  /**
   * Queue the specified task for execution and enable the VirtualProcessorUnit for activation if necessary.
   * The task must not be already queued.
   *
   * @param task the task.
   */
  void queue( @Nonnull final Runnable task )
  {
    _executor.queue( task );
  }

  /**
   * Activate the unit.
   * This involves setting current unit, invoking the activation function and clearing the current unit.
   * It is an error to invoke this method if there is already a current unit.
   *
   * @param activationFn the activation function.
   * @see Context#activate(ActivationFn)
   */
  private synchronized void activate( @Nonnull final ActivationFn activationFn )
  {
    VirtualProcessorUnitsHolder.CurrentVPU.activate( this );
    try
    {
      activationFn.invoke();
    }
    finally
    {
      VirtualProcessorUnitsHolder.CurrentVPU.deactivate( this );
    }
  }

  /**
   * Interface via which the {@link VirtualProcessorUnit} executes tasks.
   * The executor is responsible for activating the underlying {@link VirtualProcessorUnit} when required.
   * Each time the {@link VirtualProcessorUnit} is activated it will use callback to talk to executor
   * and the executor is responsible for selecting and executing tasks until it decides to return control
   * to the {@link VirtualProcessorUnit}.
   */
  interface Executor
  {
    /**
     * Initialize the executor passing in the context associated with the underlying {@link VirtualProcessorUnit}.
     *
     * @param context the context represent the associated {@link VirtualProcessorUnit}.
     */
    void init( @Nonnull Context context );

    /**
     * Queue task for execution and enable the executor for activation if necessary.
     * The task must not be already queued.
     *
     * @param task the task.
     */
    void queue( @Nonnull Runnable task );
  }

  @FunctionalInterface
  interface ActivationFn
  {
    /**
     * Callback method invoked by {@link Context#activate(ActivationFn)} to process tasks.
     */
    void invoke();
  }

  /**
   * Interface representing {@link VirtualProcessorUnit} passed to {@link Executor} during initialization.
   * This interface is designed to allow the {@link Executor} to activate the {@link VirtualProcessorUnit}
   * when it needs to execute tasks.
   */
  interface Context
  {
    /**
     * Activate the associated {@link VirtualProcessorUnit}.
     * This method MUST only be called if there is no {@link VirtualProcessorUnit} unit currently activated.
     * The activation will set the "current" VPU for the duration of the activation and invoke the
     * {@link ActivationFn#invoke()} function passed into the method.
     *
     * @param activationFn the function passed to process tasks.
     */
    void activate( @Nonnull ActivationFn activationFn );
  }
}
