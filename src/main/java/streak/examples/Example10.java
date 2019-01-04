package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;
import streak.StreakContext;

public class Example10
{
  public static void main( String[] args )
  {
    final StreakContext context = Streak.context();
    context
      .periodic( 1000 )
      .takeWhile( v -> v < 4 )
      .switchMap( v -> context.periodic( 200 ).takeWhile( e -> e < 10 ).map( e -> v + "." + e ) )
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
