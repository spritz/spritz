package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;

public class Example6
{
  public static void main( String[] args )
  {
    Streak
      .context()
      .periodic( 100 )
      .takeUntil( v -> v > 5 )
      .subscribe( new Flow.Subscriber<Integer>()
      {
        @Override
        public void onSubscribe( @Nonnull final Flow.Subscription subscription )
        {
          System.out.println( "onSubscribe(" + subscription + ")" );
        }

        @Override
        public void onNext( @Nonnull final Integer item )
        {
          System.out.println( "onNext(" + item + ")" );
        }

        @Override
        public void onError( @Nonnull final Throwable throwable )
        {
          throwable.printStackTrace();
        }

        @Override
        public void onComplete()
        {
          System.out.println( "onComplete()" );
        }
      } );
  }
}
