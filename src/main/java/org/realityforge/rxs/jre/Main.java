package org.realityforge.rxs.jre;

import javax.annotation.Nonnull;
import org.realityforge.rxs.Flow;
import org.realityforge.rxs.Rxs;
import org.realityforge.rxs.ValidatingSubscriber;

public class Main
{
  public static void main( String[] args )
  {
    Rxs
      .just( 1, 2, 3, 4 )
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
