package spritz.examples;

import spritz.Stream;

public class Example23
{
  public static void main( String[] args )
  {
    Stream.empty().lastOrError().subscribe( new LoggingSubscriber<>() );
    Stream.of( 1, 2, 3 ).lastOrError().subscribe( new LoggingSubscriber<>() );
    Stream.empty().lastOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
    Stream.of( 1, 2, 3 ).lastOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
  }
}
