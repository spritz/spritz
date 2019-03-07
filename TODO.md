## TODO

This document is essentially a list of shorthand notes describing work yet to completed.
Unfortunately it is not complete enough for other people to pick work off the list and
complete as there is too much un-said.

### High Priority Tasks

* Add `Elemental2` specific sources and sinks.

* Copy the invariant code from Arez - including the documentation generator etc.

* Experiment with using MessageChannel.send for MacroTask and compare jitter/delay between the two

* Add annotation and enhance processor so that VPUs are included in the documentation.

* Build a testing VPU/Scheduler based on ideas in [reactor-by-example](https://www.infoq.com/articles/reactor-by-example) article.

* Javadoc Doclet that generates a separate page per method in `Stream`

**Subjects**

- [ ] Create `Subject.create()`
- [ ] Create `Subject.createReplayWithMaxSize()`
- [ ] Create `Subject.createReplayWithMaxAge()`
- [ ] Create `Subject.createReplay()`
- [ ] Create `Subject.createCurrentValue(initialValue)`
- [ ] `share() == publish().refCount()`

### Scheduler

* https://github.com/spanicker/main-thread-scheduling

* https://github.com/facebook/react/blob/master/packages/scheduler/src/Scheduler.js

* Enhance scheduler so that it can schedule based on user priority/deadline as well as delay and/or period.
  These priorities indicate when the task will be executed. By default the task may be scheduled via as an
  idle task but as it approaches the deadline then it is moved to macroTask. The priorities out of react's
  scheduler and the corresponding timeout in ms are as follows

      // Times out immediately
      var IMMEDIATE_PRIORITY_TIMEOUT = -1;
      // Eventually times out
      var USER_BLOCKING_PRIORITY = 250;
      var NORMAL_PRIORITY_TIMEOUT = 5000;
      var LOW_PRIORITY_TIMEOUT = 10000;
      // Never times out
      var IDLE_PRIORITY = maxSigned31BitInt;

* Should add a queuing method in VPU that schedules according to one of these above priorities.

* Once the scheduler is in play it is likely we will want to implement code using `idle-until-urgent` strategy.
  Useful to delay some of the expensive setup for off screen stuff.
  - https://philipwalton.com/articles/idle-until-urgent/
  - https://github.com/GoogleChromeLabs/idlize/blob/master/IdleQueue.mjs
  - https://github.com/GoogleChromeLabs/idlize/blob/master/IdleValue.mjs

* Some tasks should be scheduled to current VPU and/or with similar deadlines/priorities as creating task.

* Perhaps one day the scheduler could be moved to a separate package?

### Other Tasks

* Change documentation categories and icons? to align with https://reactive.how/rxjs/

* Ensure tests all verify that predicates can accept supertypes of T

* Ensure that after every error, complete or cancel there are no timers left associated with stream.
  Probably can do this for every by enqueuing streams which can be cancelled and then checking that
  there are no uncancelled timer tasks on test complete.

* Ensure that cancel of last item prior to `onComplete` or `onError` will avoid the onComplete and onError calls.

* Ensure that cancel after `onComplete` or `onError` is a noop.

* Ensure tests verify that every stream source supports being disposed in `onNext(item)` invocation.

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

* Scheduler should use a linked list to contain tasks or some other similar mechanism as there is likely
  to be many tasks that are cancelled in flight and need to have resources releases to avoid being unnecessary
  memory pressure.

* Figure out how to implement `Stream.ofType(...)` in GWT/J2CL. It may just be that we have to attempt to
  cast and then catch failure but this may not work if we strip cast checks?

* State fields in AbstractSubject could probably be collapsed into a bit-field.

Below are the old TODO notes:

---

## To Implement

**Elemental2 Sources**

- [ ] `fromEvent(EventSource)` - Create events from DOM/JS event source.
- [ ] `fromPromise(Promise)` - This conversion is also built into several other higher-order operations (i.e. `switchMap()` so that if you map to to promise it will convert to promise).
- [ ] `fetch()` - Fetch/http controller that must make use of `AbortController`.
- [ ] `webSocket()` - WebSocket source.

**Elemental2 Sinks**

- [ ] `toPromise()` - Extract the first element from stream and return it or just return null on completion, error on error.

**Arez Sinks**

- [ ] `toComputableValue()` - Create an `arez.ComputableValue` instance from stream. It is unclear whether it should re-subscribe or dispose on error or completion signals. Otherwise it just provides the computable value.

**Filtering Operators**

- [ ] `reject(Predicate) == filter(!Predicate)`
- [ ] `skipLast(Count)` - Drops the last `Count` items emitted by stream, and emits the remaining items.
- [ ] `singleOrError()` - Emits the only item emitted by the stream, or signals a error if the stream completes after emitting 0 or more than 1 items.
- [ ] `singleOrDefault(DefaultValue)` - Emits the only item emitted by the stream, or emits `DefaultValue` if the stream completes after emitting 0 or more than 1 items.

- [ ] `reduce((accumulator, item) => {...function...}, initialValue) == scan((accumulator, item) => {...function...}, initialValue).lastOrDefault(initialValue)` - Similar to scan except only final value is emitted.

- [ ] `FilterByControlStreams(OnControlStream, OffControlStream)` - allow elements to pass after `OnControlStream` emits an element but before `OffControlStream` emits an element. Complete if both streams complete. Error if either completes.
- [ ] `TakeUntil(ControlStream)` a.k.a. `FilterByControlStreams(ControlStream.mapTo(true).startWith(true).take(2), Spritz.empty())` - take until `ControlStream` emits an element or completes.
- [ ] `SkipUntil(ControlStream)` a.k.a. `FilterByControlStreams(Spritz.empty(),ControlStream.first())` - skip until `ControlStream` emits an element or completes.

**Combination Operators** (Take 2 or more streams and combine) (a.k.a vertical merging operations as it combines values across streams)

- [ ] `combineLatest(Streams)` == `combine(Streams)` - for each stream grab latest value and pass through a function and pass on result of function this happens anytime an item is received on any stream. onComplete if all onComplete, onError if any onError
- [ ] `withLatestFrom(MasterStream,Streams)` a.k.a. `snapshot(MasterStream,Streams)` - for a primary stream, any time an item appears combine it with latest from other streams using function to produce new item. onComplete if all onComplete, onError if any onError
      a.k.a. For each event in a sampler Stream, apply a function to combine its value with the most recent event value in another Stream. The resulting Stream will contain the same number of events as the sampler Stream.
- [ ] `zip(Streams)` - select N-th value of each stream and combine them using a function. onComplete if all onComplete, onError if any onError
- [ ] `firstEmitting(Stream...)` or `selectFirstEmitting(Stream...)` or `race(Stream...)` - wait for first publisher to emit a value, select publisher and then cancel other publishers and pass on signals from selected publisher

**Accumulating Operators** (Takes 1 or more values from a single streams and combine) (a.k.a horizontal merging operations as it combines values within streams)

- [ ] `bufferByCount` - wait for Count items and then emit them as an array. onComplete send may remaining?
- [ ] `bufferByTime` - wait for time buffering items.
- [ ] `bufferByPredicate` - use predicate to determine when to emit - predicate passed each item.
- [ ] `bufferBySignal` - Another stream signals when to open and/or close buffering operation.
- [ ] `toList()` - Return all the values in stream as a list.

- [ ] `loop()` - Accumulate results using a feedback loop that emits one value and feeds back another to be used in the next iteration. It allows you to maintain and update a “state” (a.k.a. feedback, a.k.a. seed for the next iteration) while emitting a different value. In contrast, scan feeds back and produces the same value.

- [ ] `sampleBy(ControlStream)` - When `ControlStream` emits then produce a sample.

**HigherOrder Operators**

All the windowing functions take an input stream that they cut up into segments where each segment is a new stream.

- [ ] `window(ControlStream)` - Create an inner stream each time next occurs on control stream and forward all `onNext` calls onto inner stream.
- [ ] `windowByTime(WindowTime)` - Create a new inner stream every `WindowTime` time.
- [ ] `windowByCount(Count)` - Create a new inner stream every `Count` items.
- [ ] `window(OnControlStream,OffControlStream)` - Create a new inner stream every starting on signal from `OnControlStream` and then ending when signal occurs on `OffControlStream`.

Other

- [ ] `groupBy(GroupByFunction)` - Create an inner stream based on group returned by function. Stream can be concurrent.

-----

- [ ] `delayBy(DelayFunction) where DelayTime DelayFunction(Item)` - delay each item by variable time returned by delay function. This involves buffering them for a time and may result in reordered messages.
- [ ] `delay(DelayTime) == delayBy(_ -> DelayTime) ` - delay each item by DelayTime. This involves buffering them by a fixed time.
- [ ] `delaySubscriptionBy(DelayFunction) where DelayTime DelayFunction(Subscription)` - delay subscription of upstream by variable time returned by delay function.
- [ ] `delaySubscription(DelayTime) == delaySubscriptionBy(_ -> DelayTime)` - delay subscription of upstream by DelayTime
- [ ] `sort()` - buffer all items until onComplete then apply some sorting

**Control Flow**

NOTE: All of these repeats are only valid on completable streams. Can we validate this?

- [ ] `retryWhen(RetryCount, RetryWhenFunction)` - Like retry but a function indicates when should start retry.
- [ ] `retryWithExponentialBackoff(RetryCount)` - A call to `retryWhen` with some simple parameters .

Note: Several of the above functions take functions that control when an event occurs (i.e. when an event is
delayed to, when a retry occurs). These methods should also take an observable that signals when the action should
occur and will be unsubscribed from after that.

### Documentation

Steal some documentation terminology for timeline notation from https://mostcore.readthedocs.io/en/latest/notation.html#timeline-notation

Steal some general documentation concepts from https://mostcore.readthedocs.io/en/latest/concepts.html

### Sample Applications

Once done build a few sample apps.

* [React TodoMVC 1](https://github.com/RxJS-CN/react-rxjs-todos)
* [React TodoMVC 2](https://github.com/mauriciosoares/todomvc-react-rxjs)
* [TodoMVC](https://github.com/briancavalier/mostcore-todomvc)
* [rxsnake](https://github.com/ibaca/rxsnake-gwt) (Implementation described on [blog](http://philipnilsson.github.io/badness/)).
  Maybe style like in [react-game-snake](https://github.com/avin/react-game-snake). Could also look to
  [react-snake](https://github.com/taming-the-state-in-react/react-snake)
