package spritz.internal.vpu;

import javax.annotation.Nonnull;

/**
 * Interface via which the {@link VirtualProcessorUnit} executes tasks.
 * Each time the {@link VirtualProcessorUnit} is activated it will invoke {@link #executeTasks()}
 * and the executor is responsible for selecting and executing tasks until it decides to return control
 * to the caller.
 */
public interface TaskExecutor
{
  /**
   * Execute the tasks for a single activation.
   */
  void executeTasks();

  /**
   * Queue task for execution and enable the executor for activation if necessary.
   * The task must not be already queued.
   *
   * @param task the task.
   */
  void queueTask( @Nonnull Task task );
}
