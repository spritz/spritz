package streak.examples;

import streak.Streak;

public class Example19
{
  public static void main( String[] args )
  {
    Streak
      .context()
      .range( 1, 4 )
      .onNext( e -> System.out.println( "\uD83D\uDC41 onNext(" + e + ")" ) )
      .afterNext( e -> System.out.println( "\uD83D\uDC41 afterNext(" + e + ")" ) )
      .onComplete( () -> System.out.println( "\uD83D\uDC41 onComplete()" ) )
      .afterComplete( () -> System.out.println( "\uD83D\uDC41 afterComplete()" ) )
      .onError( e -> System.out.println( "\uD83D\uDC41 onError(" + e + ")" ) )
      .afterError( e -> System.out.println( "\uD83D\uDC41 afterError(" + e + ")" ) )
      .onTerminate( () -> System.out.println( "\uD83D\uDC41 onTerminate()" ) )
      .afterTerminate( () -> System.out.println( "\uD83D\uDC41 afterTerminate()" ) )
      .subscribe( new LoggingSubscriber<>() );
    System.out.println();
    System.out.println( "Second run" );
    System.out.println();
    Streak
      .context()
      .fail( new Error( "Bang!" ) )
      .onNext( e -> System.out.println( "\uD83D\uDC41 onNext(" + e + ")" ) )
      .afterNext( e -> System.out.println( "\uD83D\uDC41 afterNext(" + e + ")" ) )
      .onComplete( () -> System.out.println( "\uD83D\uDC41 onComplete()" ) )
      .afterComplete( () -> System.out.println( "\uD83D\uDC41 afterComplete()" ) )
      .onError( e -> System.out.println( "\uD83D\uDC41 onError(" + e + ")" ) )
      .afterError( e -> System.out.println( "\uD83D\uDC41 afterError(" + e + ")" ) )
      .onTerminate( () -> System.out.println( "\uD83D\uDC41 onTerminate()" ) )
      .afterTerminate( () -> System.out.println( "\uD83D\uDC41 afterTerminate()" ) )
      .subscribe( new LoggingSubscriber<>() );
  }
}
