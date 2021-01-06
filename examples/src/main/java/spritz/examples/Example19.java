package spritz.examples;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import spritz.Stream;

public class Example19
{
  public static void main( String[] args )
  {
    System.out.println();
    System.out.println( "First run" );
    System.out.println();
    ExampleUtil.run( peekAll( Stream.range( 1, 4 ) ) );
    System.out.println();
    System.out.println( "Second run" );
    System.out.println();
    ExampleUtil.run( peekAll( Stream.fail( new Error( "Bang!" ) ) ) );
    System.out.println();
    System.out.println( "Third run" );
    System.out.println();
    ExampleUtil.run( peekAll( Stream.range( 1, 5 ).takeUntil( e -> true ) ) );
  }

  @Nonnull
  private static <T> Stream<T> peekAll( @Nonnull final Stream<T> stream )
  {
    return stream
      .peekSubscribe( e1 -> System.out.println( "\uD83D\uDC41 onSubscribe(" + e1 + ")" ) )
      .afterSubscribe( e1 -> System.out.println( "\uD83D\uDC41 afterSubscribe(" + e1 + ")" ) )
      .peek( (Consumer<? super T>) e2 -> System.out.println( "\uD83D\uDC41 onItem(" + e2 + ")" ) )
      .afterNext( e -> System.out.println( "\uD83D\uDC41 afterNext(" + e + ")" ) )
      .peekComplete( () -> System.out.println( "\uD83D\uDC41 onComplete()" ) )
      .afterComplete( () -> System.out.println( "\uD83D\uDC41 afterComplete()" ) )
      .peekError( e -> System.out.println( "\uD83D\uDC41 onError(" + e + ")" ) )
      .afterError( e -> System.out.println( "\uD83D\uDC41 afterError(" + e + ")" ) )
      .peekCancel( () -> System.out.println( "\uD83D\uDC41 onCancel()" ) )
      .afterCancel( () -> System.out.println( "\uD83D\uDC41 afterCancel()" ) )
      .peekTerminate( () -> System.out.println( "\uD83D\uDC41 onTerminate()" ) )
      .afterTerminate( () -> System.out.println( "\uD83D\uDC41 afterTerminate()" ) );
  }
}
