package spritz.examples;

import spritz.Scheduler;
import spritz.Subject;

public class Example41
{
  public static void main( String[] args )
    throws Exception
  {
    final Subject<Object> value$ = Subject.createSubject();

    value$.next( Math.random() );

    value$.subscribe( new LoggingSubscriber<>( "S1" ) );
    value$.subscribe( new LoggingSubscriber<>( "S2" ) );

    value$.next( Math.random() );

    Thread.sleep( 2500 );

    value$.subscribe( new LoggingSubscriber<>( "S3" ) );

    value$.next( Math.random() );

    Scheduler.schedule( value$::complete, 100 );

    ExampleUtil.run( value$ );
  }
}
