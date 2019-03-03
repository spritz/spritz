package spritz.examples;

import spritz.Scheduler;
import spritz.Stream;

public class Example31
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .range( 42, 1 )
      .peekSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Scheduler.currentVpu() ) )
      .observeOn( Scheduler.microTaskVpu() )
      .peek( v -> System.out.println( "Peek1 on Thread: " + Scheduler.currentVpu() ) )
      .peekComplete( () -> System.out.println( "onComplete1 on Thread: " + Scheduler.currentVpu() ) )
      .subscribeOn( Scheduler.macroTaskVpu() )
      .observeOn( Scheduler.animationFrameVpu() )
      .peekSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Scheduler.currentVpu() ) )
      .peek( v -> System.out.println( "Peek2 on Thread: " + Scheduler.currentVpu() ) )
      .peekComplete( () -> System.out.println( "onComplete2 on Thread: " + Scheduler.currentVpu() ) )
      .subscribeOn( Scheduler.microTaskVpu() )
      .observeOn( Scheduler.macroTaskVpu() )
      .peek( v -> System.out.println( "Peek3 on Thread: " + Scheduler.currentVpu() ) )
      .peekComplete( () -> System.out.println( "onComplete3 on Thread: " + Scheduler.currentVpu() ) );
    ExampleUtil.run( stream );
  }
}
