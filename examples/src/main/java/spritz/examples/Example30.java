package spritz.examples;

import spritz.Stream;
import zemeckis.Scheduler;

public class Example30
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream
                       .range( 42, 1 )
                       .peekSubscribe( s -> System.out.println( "onSubscribe1 on Thread: " + Scheduler.currentVpu() ) )
                       .peek( v -> System.out.println( "Peek1 on Thread: " + Scheduler.currentVpu() ) )
                       .peekComplete( () -> System.out.println( "onComplete1 on Thread: " + Scheduler.currentVpu() ) )
                       .subscribeOn( Scheduler.macroTaskVpu() )
                       .peekSubscribe( s -> System.out.println( "onSubscribe2 on Thread: " + Scheduler.currentVpu() ) )
                       .peek( v -> System.out.println( "Peek2 on Thread: " + Scheduler.currentVpu() ) )
                       .peekComplete( () -> System.out.println( "onComplete2 on Thread: " + Scheduler.currentVpu() ) )
                       .subscribeOn( Scheduler.microTaskVpu() )
                       .peek( v -> System.out.println( "Peek3 on Thread: " + Scheduler.currentVpu() ) )
                       .peekComplete( () -> System.out.println( "onComplete3 on Thread: " +
                                                                Scheduler.currentVpu() ) ) );
  }
}
