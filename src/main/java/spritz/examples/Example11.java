package spritz.examples;

import spritz.Stream;
import spritz.schedulers.Schedulers;

public class Example11
{
  public static void main( String[] args )
  {
    Stream
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .exhaustMap( v -> Stream.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) )
      .afterTerminate( Example11::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
