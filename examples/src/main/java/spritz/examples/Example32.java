package spritz.examples;

import spritz.Stream;

public class Example32
{
  public static void main( String[] args )
  {
    final Stream<Integer> stream = Stream
      .<Integer>create( c -> c.error( new Exception( "Bad Stuff" ) ) )
      .onErrorResumeWith( s -> Stream.of( 1, 2, 3 ) );
    ExampleUtil.run( stream );
  }
}
