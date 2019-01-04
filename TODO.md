## TODO

* Ensure tests all verify that predicates can accept supertypes of T

* Ensure tests all verify that take control streams gracefully handle completion and error signals.

* Ensure tests verify scenario where stream has a `takeWhile( v -> false )` to make sure we correctly
  separate out onSubscribe from initial delivery of elements.

* Stream should mark whether they can ever emit errors or completions. This could be done statically at
  compile time, dynamically at runtime (in development mode), or dynamically at runtime using spies or ...
  This would allow us to detect poorly constructed stream like adding onError on streams that produce no
  errors or reduce on a stream that never completes.

* Each operator should supply N different marble diagram inputs. These inputs are picked up and used to
  test the operators. Some diagrams are also tagged so that a documentation tool runs over them and produces
  diagrams that can be included in javadocs and other documentation. Develop a custom javadoc plugin that
  includes marble diagrams in the generated javadocs based on the same tags. Each diagram can potentially
  have a label and a description as well as annotations inside the diagram. An even better regime would be
  to allow example code for each example. The example code is checked to ensure it matches the expected
  marble diagram and can be formatted and places in the documentation.
