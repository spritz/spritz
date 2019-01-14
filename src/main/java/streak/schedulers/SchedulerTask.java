package streak.schedulers;

/**
 * Interface to allow cancelling a queued task.
 */
public interface SchedulerTask
{
  /**
   * Cancel the task.
   * The task will not be triggered if it has not already been executed.
   * If the task has already been cancelled then this is effectively a no-op.
   */
  void cancel();

  /**
   * Return true if cancel() has been called on task.
   *
   * @return true if cancel() has been called on task.
   */
  boolean isCancelled();
}
