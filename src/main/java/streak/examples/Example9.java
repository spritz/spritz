package streak.examples;

import java.util.Arrays;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;

public class Example9
{
  public static void main( String[] args )
  {
    Streak
      .context()
      .fromCollection( Arrays.asList( "A", "B", "C", "D", "E" ) )
      .subscribe( new Flow.Subscriber<String>()
      {
        @Override
        public void onSubscribe( @Nonnull final Flow.Subscription subscription )
        {
          System.out.println( "onSubscribe(" + subscription + ")" );
        }

        @Override
        public void onNext( @Nonnull final String item )
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
