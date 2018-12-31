package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;

public class Example1
{
  public static void main( String[] args )
  {
    Streak
      .range( 42, 20 )
      .peek( v -> System.out.println( "Pre Take Peek: " + v ) )
      .take( 5 )
      .peek( v -> System.out.println( "Post Take Peek: " + v ) )
      .onTerminate( () -> System.out.println( "onTerminate()" ) )
      .subscribe( new Flow.Subscriber<Integer>()
      {
        @Override
        public void onSubscribe( @Nonnull final Flow.Subscription subscription )
        {
          System.out.println( "Range.onSubscribe(" + subscription + ")" );
        }

        @Override
        public void onNext( @Nonnull final Integer item )
        {
          System.out.println( "Range.onNext(" + item + ")" );
        }

        @Override
        public void onError( @Nonnull final Throwable throwable )
        {
          throwable.printStackTrace();
        }

        @Override
        public void onComplete()
        {
          System.out.println( "Range.onComplete()" );
        }
      } );
  }
}
