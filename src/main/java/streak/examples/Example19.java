package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;

public class Example19
{
  public static void main( String[] args )
  {
    System.out.println();
    System.out.println( "First run" );
    System.out.println();
    peekAll( Streak.context().range( 1, 4 ) ).subscribe( new LoggingSubscriber<>() );
    System.out.println();
    System.out.println( "Second run" );
    System.out.println();
    peekAll( Streak.context().fail( new Error( "Bang!" ) ) ).subscribe( new LoggingSubscriber<>() );
  }

  @Nonnull
  private static <T> Flow.Stream<T> peekAll( final Flow.Stream<T> stream )
  {
    return stream
      .onNext( e -> System.out.println( "\uD83D\uDC41 onNext(" + e + ")" ) )
      .afterNext( e -> System.out.println( "\uD83D\uDC41 afterNext(" + e + ")" ) )
      .onComplete( () -> System.out.println( "\uD83D\uDC41 onComplete()" ) )
      .afterComplete( () -> System.out.println( "\uD83D\uDC41 afterComplete()" ) )
      .onError( e -> System.out.println( "\uD83D\uDC41 onError(" + e + ")" ) )
      .afterError( e -> System.out.println( "\uD83D\uDC41 afterError(" + e + ")" ) )
      .onDispose( () -> System.out.println( "\uD83D\uDC41 onDispose()" ) )
      .afterDispose( () -> System.out.println( "\uD83D\uDC41 afterDispose()" ) )
      .onTerminate( () -> System.out.println( "\uD83D\uDC41 onTerminate()" ) )
      .afterTerminate( () -> System.out.println( "\uD83D\uDC41 afterTerminate()" ) );
  }
}
