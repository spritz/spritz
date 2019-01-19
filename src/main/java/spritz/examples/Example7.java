package spritz.examples;

import spritz.Stream;
import spritz.schedulers.Schedulers;

public class Example7
{
  public static void main( String[] args )
  {
    Stream
      .merge( Stream.periodic( 100 ).takeUntil( v -> v > 5 ).map( v -> "A" + v ),
              Stream.periodic( 50 ).takeUntil( v -> v > 30 ).map( v -> "B" + v ),
              Stream.periodic( 1000 ).takeUntil( v -> v > 3 ).map( v -> "C" + v ) )
      .afterTerminate( Example7::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
