package spritz.examples;

import javax.annotation.Nonnull;
import spritz.Spritz;
import spritz.Stream;

public class Example19
{
  public static void main( String[] args )
  {
    System.out.println();
    System.out.println( "First run" );
    System.out.println();
    peekAll( Spritz.range( 1, 4 ) ).subscribe( new LoggingSubscriber<>() );
    System.out.println();
    System.out.println( "Second run" );
    System.out.println();
    peekAll( Spritz.fail( new Error( "Bang!" ) ) ).subscribe( new LoggingSubscriber<>() );
    System.out.println();
    System.out.println( "Third run" );
    System.out.println();
    peekAll( Spritz.range( 1, 5 ).takeUntil( e -> true ) ).subscribe( new LoggingSubscriber<>() );
  }

  @Nonnull
  private static <T> Stream<T> peekAll( @Nonnull final Stream<T> stream )
  {
    return stream
      .onSubscribe( e -> System.out.println( "\uD83D\uDC41 onSubscribe(" + e + ")" ) )
      .afterSubscribe( e -> System.out.println( "\uD83D\uDC41 afterSubscribe(" + e + ")" ) )
      .onNext( e -> System.out.println( "\uD83D\uDC41 onNext(" + e + ")" ) )
      .afterNext( e -> System.out.println( "\uD83D\uDC41 afterNext(" + e + ")" ) )
      .onComplete( () -> System.out.println( "\uD83D\uDC41 onComplete()" ) )
      .afterComplete( () -> System.out.println( "\uD83D\uDC41 afterComplete()" ) )
      .onError( e -> System.out.println( "\uD83D\uDC41 onError(" + e + ")" ) )
      .afterError( e -> System.out.println( "\uD83D\uDC41 afterError(" + e + ")" ) )
      .onCancel( () -> System.out.println( "\uD83D\uDC41 onCancel()" ) )
      .afterCancel( () -> System.out.println( "\uD83D\uDC41 afterCancel()" ) )
      .onTerminate( () -> System.out.println( "\uD83D\uDC41 onTerminate()" ) )
      .afterTerminate( () -> System.out.println( "\uD83D\uDC41 afterTerminate()" ) );
  }
}