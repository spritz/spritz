package spritz.examples;

import spritz.Stream;
import spritz.schedulers.Schedulers;

public class Example10
{
  public static void main( String[] args )
  {
    Stream
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .switchMap( v -> Stream.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) )
      .afterTerminate( Example10::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
