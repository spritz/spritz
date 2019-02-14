package spritz;

import javax.annotation.Nonnull;

/**
 * The scheduler is responsible for scheduling and executing tasks asynchronously.
 * The scheduler provides an "abstract asynchronous boundary" to stream operators.
 *
 * <p>The scheduler has an internal clock that represents time as a monotonically increasing
 * <code>int</code> value. The value may or may not have a direct relationship to wall-clock
 * time and the unit of the value is defined by the implementation. Tasks can be scheduled
 * to be invoked after a delay or repeatedly with a fixed period.</p>
 *
 * <p>The scheduler assigns tasks to different Virtual Processor Unit (VPU) instances that run at
 * different phases in the browser lifecycle. Tasks can be scheduled on the:</p>
 * <ul>
 * <li>"Macro" task VPU. The normal phase where event callbacks, timer timeouts, message channel handlers etc. are invoked.</li>
 * <li>"Micro" task VPU. Invoked immediately after the current "Macro" or "Micro" task.</li>
 * <li>"animationFrame" VPU. Invoked prior to the next browser render.</li>
 * <li>"afterFrame" VPU. Invoked after the next browser render.</li>
 * <li>"onIdle" VPU. Invoked when the browser is idle.</li>
 * </ul>
 */
public final class Scheduler
{
  private Scheduler()
  {
  }

  /**
   * Return a value representing the "current time" of the scheduler.
   *
   * @return the "current time" of the scheduler.
   */
  public static int now()
  {
    return TemporalScheduler.now();
  }

  /**
   * Schedules the execution of the given task after a specified delay.
   *
   * @param task  the task to execute.
   * @param delay the delay before the task should execute. Must be a value greater than 0.
   * @return the {@link Cancelable} instance that can be used to cancel execution of the task.
   */
  @Nonnull
  public static Cancelable schedule( @Nonnull final Runnable task, final int delay )
  {
    return TemporalScheduler.schedule( task, delay );
  }

  /**
   * Schedules the periodic execution of the given task with specified period.
   *
   * @param task   the task to execute.
   * @param period the period after execution when the task should be re-executed. Must be a value greater than 0.
   * @return the {@link Cancelable} instance that can be used to cancel execution of the task.
   */
  @Nonnull
  public static Cancelable scheduleAtFixedRate( @Nonnull final Runnable task, final int period )
  {
    return TemporalScheduler.scheduleAtFixedRate( task, period );
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
}
