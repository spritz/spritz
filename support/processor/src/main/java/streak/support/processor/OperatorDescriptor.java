package streak.support.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

final class OperatorDescriptor
{
  @Nonnull
  private final ExecutableElement _method;
  private final Set<String> _categories = new HashSet<>();

  OperatorDescriptor( @Nonnull final ExecutableElement method )
  {
    _method = Objects.requireNonNull( method );
  }

  @Nonnull
  String getName()
  {
    return getMethod().getSimpleName().toString() +
           "(" +
           getMethod()
             .getParameters()
             .stream()
             .map( p -> ( (VariableElement) p ).getSimpleName().toString() )
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
}
