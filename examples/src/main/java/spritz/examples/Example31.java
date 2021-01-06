package spritz.examples;

import spritz.Stream;
import zemeckis.Zemeckis;

public class Example31
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .range( 42, 1 )
      .peekSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Zemeckis.currentVpu() ) )
      .observeOn( Zemeckis.microTaskVpu() )
      .peek( v -> System.out.println( "Peek1 on Thread: " + Zemeckis.currentVpu() ) )
      .peekComplete( () -> System.out.println( "onComplete1 on Thread: " + Zemeckis.currentVpu() ) )
      .subscribeOn( Zemeckis.macroTaskVpu() )
      .observeOn( Zemeckis.animationFrameVpu() )
      .peekSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Zemeckis.currentVpu() ) )
      .peek( v -> System.out.println( "Peek2 on Thread: " + Zemeckis.currentVpu() ) )
      .peekComplete( () -> System.out.println( "onComplete2 on Thread: " + Zemeckis.currentVpu() ) )
      .subscribeOn( Zemeckis.microTaskVpu() )
      .observeOn( Zemeckis.macroTaskVpu() )
      .peek( v -> System.out.println( "Peek3 on Thread: " + Zemeckis.currentVpu() ) )
      .peekComplete( () -> System.out.println( "onComplete3 on Thread: " + Zemeckis.currentVpu() ) );
    ExampleUtil.run( stream );
  }
}
