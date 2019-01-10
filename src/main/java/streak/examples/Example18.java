package streak.examples;

import java.util.concurrent.atomic.AtomicInteger;
import streak.Streak;
import streak.schedulers.Schedulers;

public class Example18
{
  public static void main( String[] args )
  {
    final AtomicInteger counter = new AtomicInteger();
    Streak
      .generate( counter::incrementAndGet, 50 )
      .throttleLatest( 210 )
      .take( 12 )
      .afterTerminate( Example18::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( () -> Schedulers.current().shutdown() ).run();
  }
}
