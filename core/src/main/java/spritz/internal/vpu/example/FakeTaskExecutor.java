package spritz.internal.vpu.example;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import spritz.internal.vpu.ExecutorContext;
import spritz.internal.vpu.FifoTaskQueue;
import spritz.internal.vpu.RoundBasedTaskExecutor;
import spritz.internal.vpu.Task;
import spritz.internal.vpu.TaskExecutor;
import spritz.internal.vpu.TaskQueue;
import spritz.internal.vpu.VirtualProcessorUnit;

public class FakeTaskExecutor
  implements TaskExecutor
{
  public static VirtualProcessorUnit VPU1 = new VirtualProcessorUnit( new FakeTaskExecutor( "VPU1" ) );
  public static VirtualProcessorUnit VPU2 = new VirtualProcessorUnit( new FakeTaskExecutor( "VPU2" ) );
  public static VirtualProcessorUnit VPU3 = new VirtualProcessorUnit( new FakeTaskExecutor( "VPU3" ) );
  public static VirtualProcessorUnit VPU4 = new VirtualProcessorUnit( new FakeTaskExecutor( "VPU4" ) );
  private final TaskQueue _taskQueue = new FifoTaskQueue( 100 );
  private final RoundBasedTaskExecutor _executor = new RoundBasedTaskExecutor( _taskQueue, 100 );
  private final ScheduledExecutorService _executorService =
    new ScheduledThreadPoolExecutor( 1, this::newThread );
  @Nonnull
  private final String _name;

  private FakeTaskExecutor( @Nonnull final String name )
  {
    _name = name;
  }

  @Nonnull
  private Thread newThread( @Nonnull final Runnable r )
  {
    return new Thread( r, _name );
  }

  private ExecutorContext _context;

  @Override
  public void init( @Nonnull final ExecutorContext context )
  {
    _context = context;
  }

  @Override
  public void queueTask( @Nonnull final Task task )
  {
    if ( !_taskQueue.hasTasks() )
    {
      _executorService.schedule( this::activate, 0, TimeUnit.MILLISECONDS );
    }
    _taskQueue.queueTask( task );
  }

  private void activate()
  {
    _context.activate( _executor::executeTasks );
  }
}
