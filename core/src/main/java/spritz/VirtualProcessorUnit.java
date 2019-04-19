package spritz;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static org.realityforge.braincheck.Guards.*;

/**
 * Processing unit responsible for executing tasks.
 */
public final class VirtualProcessorUnit
{
  /**
   * A human consumable name for node. It should be non-null if {@link Spritz#areNamesEnabled()} returns
   * true and <tt>null</tt> otherwise.
   */
  @Nullable
  private final String _name;
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
  VirtualProcessorUnit( @Nullable final String name, @Nonnull final Executor executor )
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( () -> Spritz.areNamesEnabled() || null == name,
                    () -> "Spritz-0021: VirtualProcessorUnit passed a name '" + name + "' but " +
                          "Spritz.areNamesEnabled() is false" );
    }
    _name = Spritz.areNamesEnabled() ? Objects.requireNonNull( name ) : null;
    _executor = Objects.requireNonNull( executor );
    _executor.init( activationFn -> VirtualProcessorUnitsHolder.activate( this, activationFn ) );
  }

  /**
   * Return the name of the VirtualProcessorUnit.
   * This method should NOT be invoked unless {@link Spritz#areNamesEnabled()} returns true and will throw an
   * exception if invariant checking is enabled.
   *
   * @return the name of the VirtualProcessorUnit.
   */
  @Nonnull
  public final String getName()
  {
    if ( Spritz.shouldCheckApiInvariants() )
    {
      apiInvariant( Spritz::areNamesEnabled,
                    () -> "Spritz-0022: VirtualProcessorUnit.getName() invoked when Spritz.areNamesEnabled() is false" );
    }
    assert null != _name;
    return _name;
  }

  /**
   * Queue task for execution and enable the executor for activation if necessary.
   * The task must not be already queued.
   *
   * @param task the task.
   */
  public void queue( @Nonnull final Runnable task )
  {
    getExecutor().queue( task );
  }

  @Nonnull
  Executor getExecutor()
  {
    return _executor;
  }

  @Nonnull
  @Override
  public final String toString()
  {
    if ( Spritz.areNamesEnabled() )
    {
      return getName();
    }
    else
    {
      return super.toString();
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

    /**
     * Queue task for execution next. The executor is not activated. The task must not be already queued.
     *
     * @param task the task.
     */
    void queueNext( @Nonnull Runnable task );

    /**
     * Activate the executor.
     */
    void activate();
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
