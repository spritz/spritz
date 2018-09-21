package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;
import streak.ValidatingSubscriber;

public class Main
{
  public static void main( String[] args )
  {
    Streak
      .range( 42, 20 )
      .take( 5 )
      .subscribe( new ValidatingSubscriber<>( new Flow.Subscriber<Integer>()
      {
        @Override
        public void onSubscribe( @Nonnull final Flow.Subscription subscription )
        {
          System.out.println( "Range.onSubscribe(" + subscription + ")" );
          subscription.request( 1000 );
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
      } ) );

    Streak
      .of( 1, 2, 3, 4 )
      .filter( v -> v > 2 )
      .first()
      .subscribe( new ValidatingSubscriber<>( new Flow.Subscriber<Integer>()
      {
        @Override
        public void onSubscribe( @Nonnull final Flow.Subscription subscription )
        {
          System.out.println( "Just.onSubscribe(" + subscription + ")" );
          subscription.request( 1000 );
        }

        @Override
        public void onNext( @Nonnull final Integer item )
        {
          System.out.println( "Just.onNext(" + item + ")" );
        }

        @Override
        public void onError( @Nonnull final Throwable throwable )
        {
          throwable.printStackTrace();
        }

        @Override
        public void onComplete()
        {
          System.out.println( "Just.onComplete()" );
        }
      } ) );

    Streak
      .range( 42, 20 )
      .skipUntil( v -> v == 55 )
      .map( v -> "*" + v + "*" )
      .forEach( v -> System.out.println( "Bang! " + v ) );
  }
}
