package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;
import streak.ValidatingSubscriber;

public class Example4
{
  public static void main( String[] args )
  {
    Streak
      .of( 1, 1, 1, 1, 1, 2, 2, 2, 1, 3, 3, 4 )
      .distinctInSuccession()
      .subscribe( new ValidatingSubscriber<>( new Flow.Subscriber<Integer>()
      {
        @Override
        public void onSubscribe( @Nonnull final Flow.Subscription subscription )
        {
          System.out.println( "onSubscribe(" + subscription + ")" );
          subscription.request( 1000 );
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
      } ) );
  }
}
