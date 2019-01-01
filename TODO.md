## TODO

* Ensure tests all verify that predicates can accept supertypes of T

* Stream should mark whether they can ever emit errors or completions. This could be done statically at
  compile time, dynamically at runtime (in development mode), or dynamically at runtime using spies or ...
  This would allow us to detect poorly constructed stream like adding onError on streams that produce no
  errors or reduce on a stream that never completes.
