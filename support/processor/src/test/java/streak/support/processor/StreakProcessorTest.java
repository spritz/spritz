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
        new Object[]{ "streak.MultipleCategoriesStream" },
        new Object[]{ "streak.MultipleOperatorsStream" },
        new Object[]{ "streak.NonOperatorMethodsStream" },
        new Object[]{ "streak.OperatorWithParametersStream" },
        new Object[]{ "streak.OverloadedOperatorsStream" }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname );
  }
}
