package spritz.examples;

import spritz.Spritz;
import spritz.schedulers.Schedulers;

public class Example17
{
  public static void main( String[] args )
  {
    Spritz
      .generate( () -> "Tick", 200 )
      .take( 12 )
      .afterTerminate( Example17::terminateScheduler )
      .subscribe( new LoggingSubscriber<>() );
  }

  private static void terminateScheduler()
  {
    new Thread( Schedulers::shutdown ).run();
  }
}
