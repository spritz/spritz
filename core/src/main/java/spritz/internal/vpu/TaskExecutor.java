package spritz.internal.vpu;

import javax.annotation.Nonnull;

/**
 * Interface via which the {@link VirtualProcessorUnit} executes tasks.
 * The executor is responsible for activating the underlying {@link VirtualProcessorUnit} when required.
 * Each time the {@link VirtualProcessorUnit} is activated it will use callback to talk to executor
 * and the executor is responsible for selecting and executing tasks until it decides to return control
 * to the {@link VirtualProcessorUnit}.
 */
public interface TaskExecutor
{
  /**
   * Initialize the executor passing in the context associated with the underlying {@link VirtualProcessorUnit}.
   *
   * @param context the context represent the associated {@link VirtualProcessorUnit}.
   */
  void init( @Nonnull ExecutorContext context );

  /**
   * Queue task for execution and enable the executor for activation if necessary.
   * The task must not be already queued.
   *
   * @param task the task.
   */
  void queueTask( @Nonnull Task task );
}
