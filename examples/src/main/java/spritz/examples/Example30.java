package spritz.examples;

import spritz.Stream;
import spritz.internal.vpu.example.FakeTaskExecutor;

public class Example30
{
  public static void main( String[] args )
    throws Exception
  {
    Stream
      .range( 42, 1 )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Thread.currentThread().getName() ) )
      .peek( v -> System.out.println( "Peek1 on Thread: " + Thread.currentThread().getName() ) )
      .subscribeOn( FakeTaskExecutor.VPU1 )
      .onSubscribe( s -> System.out.println( "onSubscribe on Thread: " + Thread.currentThread().getName() ) )
      .peek( v -> System.out.println( "Peek2 on Thread: " + Thread.currentThread().getName() ) )
      .subscribeOn( FakeTaskExecutor.VPU2 )
      .peek( v -> System.out.println( "Peek3 on Thread: " + Thread.currentThread().getName() ) )
      .subscribe( new LoggingSubscriber<>() );
    Thread.sleep( 1000 );
  }
}
