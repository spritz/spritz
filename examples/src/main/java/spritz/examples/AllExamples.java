package spritz.examples;

public class AllExamples
{
  public static void main( String[] args )
  {
    for ( int i = 1; i < 1000; i++ )
    {
      try
      {
        final Class<?> exampleType = Class.forName( "spritz.examples.Example" + i );
        final Object[] params = { new String[ 0 ] };
        System.out.println( "\n\n\nRunning Example" + i + ".main()\n" );
        exampleType.getDeclaredMethod( "main", String[].class ).invoke( null, params );
      }
      catch ( final ClassNotFoundException cnfe )
      {
        // Got to the end of the examples
        return;
      }
      catch ( final Throwable t )
      {
        t.printStackTrace();
        System.exit( 2 );
      }
    }
  }
}
