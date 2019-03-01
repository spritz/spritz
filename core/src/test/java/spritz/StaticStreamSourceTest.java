package spritz;

import javax.annotation.Nonnull;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class StaticStreamSourceTest
  extends AbstractSpritzTest
{
  @Test
  public void getName_default()
  {
    final Stream<Integer> stream = Stream.of( 1, 2, 3 );
    assertEquals( stream.getName(), "of(1, 2, 3)" );
  }

  @Test
  public void getName_specified()
  {
    final Stream<Integer> stream = Stream.of( "dataIn()", 1, 2, 3 );
    assertEquals( stream.getName(), "dataIn()" );
  }

  @Test
  public void x()
  {
    final Stream<Integer> stream = Stream.of( 1, 2, 3 );
    assertEquals( stream.getName(), "of(1, 2, 3)" );

    stream.subscribe( new Subscriber<Integer>()
    {
      @Override
      public void onSubscribe( @Nonnull final Subscription subscription )
      {
        assertEquals( subscription.toString(), "of(1, 2, 3)" );
      }

      @Override
      public void onNext( @Nonnull final Integer item )
      {

      }

      @Override
      public void onError( @Nonnull final Throwable error )
      {
        fail( "Unexpected error signal" );
      }

      @Override
      public void onComplete()
      {

      }
    } );
  }
}
