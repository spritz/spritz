package streak.support.processor;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.json.stream.JsonGenerator;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;

final class OperatorDescriptor
{
  @Nonnull
  private final ExecutableElement _method;
  @Nonnull
  private final ExecutableType _methodType;
  private final Set<String> _categories = new HashSet<>();

  OperatorDescriptor( @Nonnull final ExecutableElement method, @Nonnull final ExecutableType methodType )
  {
    _method = Objects.requireNonNull( method );
    _methodType = Objects.requireNonNull( methodType );
  }

  @Nonnull
  String getName()
  {
    return getMethod().getSimpleName().toString() +
           "(" +
           getMethod()
             .getParameters()
             .stream()
             .map( p -> p.getSimpleName().toString() )
             .collect( Collectors.joining( "," ) ) +
           ")";
  }

  @Nonnull
  ExecutableElement getMethod()
  {
    return _method;
  }

  void addCategory( @Nonnull final String category )
  {
    _categories.add( Objects.requireNonNull( category ) );
  }

  List<String> getCategories()
  {
    return _categories.stream().sorted().collect( Collectors.toList() );
  }

  @Nonnull
  private String getJavadocLink()
  {
    final TypeElement typeName = (TypeElement) getMethod().getEnclosingElement();
    return typeName.getQualifiedName().toString() +
           "#" +
           getMethod().getSimpleName().toString() +
           "(" +
           _methodType
             .getParameterTypes()
             .stream()
             .map( p -> toRawType( TypeName.get( p ) ).toString() )
             .collect( Collectors.joining( "," ) ) +
           ")";
  }

  @Nonnull
  private TypeName toRawType( @Nonnull final TypeName paramType )
  {
    if ( paramType instanceof ParameterizedTypeName )
    {
      return ( (ParameterizedTypeName) paramType ).rawType;
    }
    else if ( paramType instanceof TypeVariableName )
    {
      final List<TypeName> bounds = ( (TypeVariableName) paramType ).bounds;
      if ( bounds.isEmpty() )
      {
        return TypeName.OBJECT;
      }
      else
      {
        return toRawType( bounds.get( 0 ) );
      }
    }
    else if ( paramType instanceof ArrayTypeName )
    {
      return ArrayTypeName.of( toRawType( ( (ArrayTypeName) paramType ).componentType ) );
    }
    else
    {
      return paramType;
    }
  }

  void write( @Nonnull final JsonGenerator generator )
  {
    generator.writeStartObject();
    generator.write( "name", getName() );
    generator.write( "link", getJavadocLink() );
    generator.writeStartArray( "categories" );
    getCategories().forEach( generator::write );
    generator.writeEnd();
    generator.writeEnd();
  }
}
