package spritz.examples;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import spritz.Task;
import spritz.VirtualProcessorUnit;
import spritz.internal.vpu.RoundBasedTaskExecutor;

public class FakeExecutor
  implements VirtualProcessorUnit.Executor
{
  static VirtualProcessorUnit VPU1 = new VirtualProcessorUnit( new FakeExecutor( "VPU1" ) );
  static VirtualProcessorUnit VPU2 = new VirtualProcessorUnit( new FakeExecutor( "VPU2" ) );
  static VirtualProcessorUnit VPU3 = new VirtualProcessorUnit( new FakeExecutor( "VPU3" ) );
  static VirtualProcessorUnit VPU4 = new VirtualProcessorUnit( new FakeExecutor( "VPU4" ) );
  private final RoundBasedTaskExecutor _executor = new RoundBasedTaskExecutor( 100 );
  private final ScheduledExecutorService _executorService = new ScheduledThreadPoolExecutor( 1, this::newThread );
  @Nonnull
  private final String _name;

  private FakeExecutor( @Nonnull final String name )
  {
    _name = name;
  }

  @Nonnull
  private Thread newThread( @Nonnull final Runnable r )
  {
    return new Thread( r, _name );
  }

  private VirtualProcessorUnit.Context _context;

  @Override
  public void init( @Nonnull final VirtualProcessorUnit.Context context )
  {
    _context = context;
  }

  @Override
  public synchronized void queue( @Nonnull final Task task )
  {
    if ( 0 == _executor.getTaskQueueSize() )
    {
      _executorService.schedule( this::activate, 0, TimeUnit.MILLISECONDS );
    }
    _executor.queue( task );
  }

  private synchronized void activate()
  {
    _context.activate( _executor::executeTasks );
  }
}
