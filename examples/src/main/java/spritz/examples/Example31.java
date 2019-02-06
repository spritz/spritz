package spritz.examples;

import spritz.Stream;
import spritz.VirtualProcessorUnit;

public class Example31
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .range( 42, 1 )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + VirtualProcessorUnit.current() ) )
      .observeOn( VirtualProcessorUnit.microTask() )
      .peek( v -> System.out.println( "Peek1 on Thread: " + VirtualProcessorUnit.current() ) )
      .onComplete( () -> System.out.println( "onComplete1 on Thread: " + VirtualProcessorUnit.current() ) )
      .subscribeOn( VirtualProcessorUnit.macroTask() )
      .observeOn( VirtualProcessorUnit.animationFrame() )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + VirtualProcessorUnit.current() ) )
      .peek( v -> System.out.println( "Peek2 on Thread: " + VirtualProcessorUnit.current() ) )
      .onComplete( () -> System.out.println( "onComplete2 on Thread: " + VirtualProcessorUnit.current() ) )
      .subscribeOn( VirtualProcessorUnit.microTask() )
      .observeOn( VirtualProcessorUnit.macroTask() )
      .peek( v -> System.out.println( "Peek3 on Thread: " + VirtualProcessorUnit.current() ) )
      .onComplete( () -> System.out.println( "onComplete3 on Thread: " + VirtualProcessorUnit.current() ) );
    ExampleUtil.run( stream );
  }
}
