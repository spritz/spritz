## TODO

* Ensure tests all verify that predicates can accept supertypes of T

* Every callable/supplier/predicate/runnable/function passed to an operator should be tested to ensure
  errors are caught and emitted as error signals.

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

* Use consistent terminology throughout the javadocs and code.
  - Use `item` or `element` to refer to the data elements that are passed downstream. At the moment the
    code uses both terms but one should be selected and the other purged. Consider renaming `onNext` to
    `onItem` or `onElement` to align with the selected terminology.
  - Use `emit` rather than `notify` or `send` to describe passing items or signals downstream.
  - Use the term `signal` rather than `notification` or `event` or `message` when referring to invoking
    the `onError` or `onComplete` methods in downstream. It is unclear if there is a consistent term for
    passing the data element downstream.
  - downstream and upstream seem reasonably useful descriptions defining which direction data is flowing.
    However we don't have a good way to describe distinguish between each step or stage and the complete
    stream definition nor the stream executable data (which is really a chain of subscriptions). `Stage`
    seems like a useful term but the codebase has no consistent terminology at this point.
