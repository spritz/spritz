package streak.examples;

import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;
import streak.StreakContext;

public class Example7
{
  @SuppressWarnings( "unchecked" )
  public static void main( String[] args )
  {
    final StreakContext context = Streak.context();
    context
      .merge( context.periodic( 100 ).takeUntil( v -> v > 5 ).map( v -> "A" + v ),
              context.periodic( 50 ).takeUntil( v -> v > 30 ).map( v -> "B" + v ),
              context.periodic( 1000 ).takeUntil( v -> v > 3 ).map( v -> "C" + v ) )
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
