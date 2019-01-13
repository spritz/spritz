package streak.support.processor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

final class ClassDescriptor
{
  @Nonnull
  private final TypeElement _typeElement;
  private final Map<ExecutableElement, OperatorDescriptor> _operators = new HashMap<>();

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
    _operators.put( operator.getMethod(), operator );
  }

  public List<OperatorDescriptor> getOperators()
  {
    return _operators.values()
      .stream()
      .sorted( Comparator.comparing( OperatorDescriptor::getName ) )
      .collect( Collectors.toList() );
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