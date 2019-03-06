package spritz.examples;

import spritz.Stream;

public class Example3
{
  public static void main( String[] args )
  {
    ExampleUtil.run( Stream.range( 42, 20 ).skipUntil( v -> v == 55 ).map( v -> "*" + v + "*" ),
                     s -> s.forEach( v -> System.out.println( "Bang! " + v ) ) );
  }
}
