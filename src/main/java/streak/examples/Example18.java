package streak.examples;

import arez.Disposable;
import java.util.concurrent.atomic.AtomicInteger;
import streak.Streak;
import streak.schedulers.Schedulers;

public class Example18
{
  public static void main( String[] args )
  {
    final AtomicInteger counter = new AtomicInteger();
    Streak.context()
      .generate( counter::incrementAndGet, 50 )
      .throttleLatest( 210 )
      .take( 12 )
      .onTerminate( Example18::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( () -> Disposable.dispose( Schedulers.current() ) ).run();
  }
}
