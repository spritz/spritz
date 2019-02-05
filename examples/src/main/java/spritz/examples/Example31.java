package spritz.examples;

import spritz.Stream;

public class Example31
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .range( 42, 1 )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Thread.currentThread().getName() ) )
      .observeOn( FakeExecutor.VPU2 )
      .peek( v -> System.out.println( "Peek1 on Thread: " + Thread.currentThread().getName() ) )
      .onComplete( () -> System.out.println( "onComplete1 on Thread: " + Thread.currentThread().getName() ) )
      .subscribeOn( FakeExecutor.VPU1 )
      .observeOn( FakeExecutor.VPU3 )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Thread.currentThread().getName() ) )
      .peek( v -> System.out.println( "Peek2 on Thread: " + Thread.currentThread().getName() ) )
      .onComplete( () -> System.out.println( "onComplete2 on Thread: " + Thread.currentThread().getName() ) )
      .subscribeOn( FakeExecutor.VPU2 )
      .observeOn( FakeExecutor.VPU4 )
      .peek( v -> System.out.println( "Peek3 on Thread: " + Thread.currentThread().getName() ) )
      .onComplete( () -> System.out.println( "onComplete3 on Thread: " + Thread.currentThread().getName() ) );
    ExampleUtil.run( stream );
  }
}
