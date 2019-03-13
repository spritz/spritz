package spritz.examples;

import spritz.Scheduler;
import spritz.Stream;
import spritz.Subject;
import spritz.Subscription;

public class Example42
{
  public static void main( String[] args )
  {
    final Subject<Object> value$ = Stream.subject();

    value$.next( Math.random() );

    final Subscription s1 = value$.subscribe( new LoggingSubscriber<>( "S1" ) );
    value$.subscribe( new LoggingSubscriber<>( "S2" ) );

    value$.next( Math.random() );

    s1.cancel();

    value$.next( Math.random() );

    Scheduler.schedule( value$::complete, 100 );

    ExampleUtil.run( value$ );
  }
}
