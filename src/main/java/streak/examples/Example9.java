package streak.examples;

import java.util.Arrays;
import javax.annotation.Nonnull;
import streak.Flow;
import streak.Streak;
import streak.StreakContext;

@SuppressWarnings( "unchecked" )
public class Example9
{
  public static void main( String[] args )
  {
    final StreakContext context = Streak.context();
    context
      .fromCollection( Arrays.asList( "A", "B", "C", "D", "E" ) )
      .append( context.of( "F", "G" ), context.of( "H", "I" ) )
      .prepend( context.of( "1", "2" ), context.of( "3", "4" ) )
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
