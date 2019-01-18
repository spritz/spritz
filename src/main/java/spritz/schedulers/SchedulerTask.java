package spritz.schedulers;

/**
 * Interface to allow cancelling a queued task.
 */
public interface SchedulerTask
{
  /**
   * Cancel the task.
   * The task will not be triggered if it has not already been executed.
   * This task should be cancelled at most once.
   */
  void cancel();
}
