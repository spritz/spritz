package spritz.support.processor;

import javax.annotation.Nonnull;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SpritzProcessorTest
  extends AbstractStreakProcessorTest
{
  @DataProvider( name = "successfulCompiles" )
  public Object[][] successfulCompiles()
  {
    return new Object[][]
      {
        new Object[]{ "spritz.BasicStream" },
        new Object[]{ "spritz.ConstructorWithComments" },
        new Object[]{ "spritz.MultipleCategoriesStream" },
        new Object[]{ "spritz.MultipleOperatorsStream" },
        new Object[]{ "spritz.NonOperatorMethodsStream" },
        new Object[]{ "spritz.OperatorWithArrayParameterStream" },
        new Object[]{ "spritz.OperatorWithParametersStream" },
        new Object[]{ "spritz.OperatorWithTypeParameterStream" },
        new Object[]{ "spritz.OverloadedOperatorsStream" },
        new Object[]{ "spritz.StreakBaseConstructor" }
      };
  }

  @Test( dataProvider = "successfulCompiles" )
  public void processSuccessfulCompile( @Nonnull final String classname )
    throws Exception
  {
    assertSuccessfulCompile( classname );
  }
}
