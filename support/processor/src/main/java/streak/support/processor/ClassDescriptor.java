package streak.support.processor;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.TypeElement;

final class ClassDescriptor
{
  @Nonnull
  private final TypeElement _typeElement;
  private final ArrayList<OperatorDescriptor> _operators = new ArrayList<>();

  ClassDescriptor( @Nonnull final TypeElement typeElement )
  {
    _typeElement = typeElement;
  }

  @Nonnull
  TypeElement getTypeElement()
  {
    return _typeElement;
  }

  void addOperator( @Nonnull final OperatorDescriptor operator )
  {
    _operators.add( operator );
  }

  public List<OperatorDescriptor> getOperators()
  {
    return _operators;
  }

  void write( @Nonnull final JsonGenerator generator )
  {
    generator.writeStartObject();
    generator.write( "class", getTypeElement().getQualifiedName().toString() );
    final List<OperatorDescriptor> operators = getOperators();
    if ( !operators.isEmpty() )
    {
      generator.writeStartArray( "operators" );
      operators.forEach( operator -> operator.write( generator ) );
      generator.writeEnd();
    }
    generator.writeEnd();
  }
}
