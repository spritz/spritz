package spritz.examples;

import spritz.Scheduler;
import spritz.Stream;

public class Example31
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .range( 42, 1 )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Scheduler.current() ) )
      .observeOn( Scheduler.microTask() )
      .peek( v -> System.out.println( "Peek1 on Thread: " + Scheduler.current() ) )
      .onComplete( () -> System.out.println( "onComplete1 on Thread: " + Scheduler.current() ) )
      .subscribeOn( Scheduler.macroTask() )
      .observeOn( Scheduler.animationFrame() )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Scheduler.current() ) )
      .peek( v -> System.out.println( "Peek2 on Thread: " + Scheduler.current() ) )
      .onComplete( () -> System.out.println( "onComplete2 on Thread: " + Scheduler.current() ) )
      .subscribeOn( Scheduler.microTask() )
      .observeOn( Scheduler.macroTask() )
      .peek( v -> System.out.println( "Peek3 on Thread: " + Scheduler.current() ) )
      .onComplete( () -> System.out.println( "onComplete3 on Thread: " + Scheduler.current() ) );
    ExampleUtil.run( stream );
  }
}
