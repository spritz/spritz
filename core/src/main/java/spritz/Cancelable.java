package spritz;

/**
 * Interface identifying a resource that can be cancelled.
 */
public interface Cancelable
{
  /**
   * Cancel the resource.
   */
  void cancel();
}
