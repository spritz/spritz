package streak.support.processor;

import javax.annotation.Nonnull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class StreakProcessorTest
  extends AbstractStreakProcessorTest
{
  @DataProvider( name = "successfulCompiles" )
  public Object[][] successfulCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "streak.BasicStream" },
        new Object[]{ "streak.ConstructorWithComments" },
        new Object[]{ "streak.MultipleCategoriesStream" },
        new Object[]{ "streak.MultipleOperatorsStream" },
        new Object[]{ "streak.NonOperatorMethodsStream" },
        new Object[]{ "streak.OperatorWithArrayParameterStream" },
        new Object[]{ "streak.OperatorWithParametersStream" },
        new Object[]{ "streak.OperatorWithTypeParameterStream" },
        new Object[]{ "streak.OverloadedOperatorsStream" },
        new Object[]{ "streak.StreakBaseConstructor" }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname );
  }

  @Test
  public void XXX()
    throws Exception
  {
    assertSuccessfulCompile( "streak.ConstructorWithComments" );
  }
}
