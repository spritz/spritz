package spritz.examples;

import spritz.Stream;

public class Example22
{
  public static void main( String[] args )
  {
    Stream.empty().firstOrError().subscribe( new LoggingSubscriber<>() );
    Stream.of( 1 ).firstOrError().subscribe( new LoggingSubscriber<>() );
    Stream.empty().firstOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
    Stream.of( 1 ).firstOrDefault( 22 ).subscribe( new LoggingSubscriber<>() );
  }
}
